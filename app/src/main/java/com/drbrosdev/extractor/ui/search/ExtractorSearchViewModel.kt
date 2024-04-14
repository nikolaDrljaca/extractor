package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.PerformSearch
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import com.drbrosdev.extractor.domain.usecase.image.search.SearchStrategy
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRangeAsFlow
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndicesAsFlow
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.isSuccess
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorsearchview.isBlank
import com.drbrosdev.extractor.ui.components.extractorsearchview.keywordTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.queryAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.searchTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorSearchViewModel(
    query: String,
    keywordType: KeywordType,
    private val imageSearch: PerformSearch,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val albumRepository: AlbumRepository,
    private val stateHandle: SavedStateHandle,
    private val generateSuggestedKeywords: GenerateSuggestedKeywords,
    private val spawnExtractorWork: SpawnExtractorWork,
    private val datastore: ExtractorDataStore,
    private val stringProvider: StringResourceProvider
) : ViewModel() {
    val snackbarHostState = SnackbarHostState()
    val extractorStatusButtonState = ExtractorStatusButtonState()

    val searchViewState = stateHandle.saveable(
        key = "extractor_search_view_state",
        saver = ExtractorSearchViewState.Saver
    ) {
        ExtractorSearchViewState(
            initialQuery = stateHandle["query"] ?: query,
            initialKeywordType = keywordType,
        )
    }

    val dateFilterState = ExtractorDateFilterState()

    val loaderButtonState = ExtractorLoaderButtonState()

    val gridState = ExtractorImageGridState()

    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.ShowSuggestions(
            ExtractorSuggestedSearchState.Loading
        )
    )
    val state = _state.asStateFlow()

    private val lastQuery = MutableStateFlow(LastQuery(query, keywordType))

    private val _events = Channel<ExtractorSearchScreenEvents>()
    val events = _events.receiveAsFlow()

    val shouldShowSheetFlow = datastore.shouldShowSearchSheet

    val searchCount = datastore.searchCount
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            0
        )

    val sheetContent = gridState.checkedIndicesAsFlow()
        .map { checked ->
            val content = when {
                checked.isNotEmpty() -> SheetContent.MultiselectBar
                else -> SheetContent.SearchView
            }
            content
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            SheetContent.SearchView
        )

    private val progressJob = trackExtractionProgress.invoke()
        .distinctUntilChanged()
        //handle create album button state
        .onEach {
            val status = when (it) {
                is ExtractionStatus.Done -> when {
                    it.isDataIncomplete -> ExtractorStatusButtonState.Status.OutOfSync
                    else -> ExtractorStatusButtonState.Status.Idle
                }

                is ExtractionStatus.Running -> ExtractorStatusButtonState.Status.ExtractionRunning(
                    it.percentage
                )
            }
            extractorStatusButtonState.update(status)
        }
        //handle show suggestions
        .onEach {
            when (it) {
                is ExtractionStatus.Done -> {
                    when {
                        ((it.inStorageCount != 0) and (_state.value !is ExtractorSearchScreenUiState.Content)) -> {
                            val suggested = generateSuggestedKeywords.invoke()
                            _state.update {
                                suggested.fold(
                                    ifRight = { result ->
                                        ExtractorSearchScreenUiState.ShowSuggestions(
                                            ExtractorSuggestedSearchState.Content(result)
                                        )
                                    },
                                    ifLeft = {
                                        ExtractorSearchScreenUiState.NoSearchesLeft
                                    }
                                )
                            }
                        }

                        else -> {
                            _state.update {
                                ExtractorSearchScreenUiState.ShowSuggestions(
                                    ExtractorSuggestedSearchState.Empty
                                )
                            }
                        }
                    }
                }

                is ExtractionStatus.Running -> _state.update {
                    ExtractorSearchScreenUiState.StillIndexing
                }
            }

        }
        .launchIn(viewModelScope)

    private val labelTypeUpdateFlow = searchViewState.keywordTypeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .onEach { loaderButtonState.initial() }
        .launchIn(viewModelScope)

    private val searchTypeUpdateFlow = searchViewState.searchTypeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .onEach { loaderButtonState.initial() }
        .launchIn(viewModelScope)

    private val saveQueryJob = searchViewState.queryAsFlow()
        .distinctUntilChanged()
        .onEach { loaderButtonState.initial() }
        .launchIn(viewModelScope)

    private val dateFilterJob = dateFilterState.dateRangeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    private val disableSearchJob = datastore.searchCount
        .distinctUntilChanged()
        .onEach {
            when {
                it == 0 -> searchViewState.disable()
                else -> searchViewState.enable()
            }
        }
        .launchIn(viewModelScope)

    private val loaderButtonEnabledJob = state
        .onEach {
            when (it) {
                is ExtractorSearchScreenUiState.Content -> loaderButtonState.enable()
                else -> loaderButtonState.disable()
            }
        }
        .launchIn(viewModelScope)

    fun performSearch(searchStrategy: SearchStrategy) {
        when (searchStrategy) {
            SearchStrategy.NORMAL -> runSearch()
            SearchStrategy.DIRTY_CHECKING -> {
                if (searchViewState.isBlank()) return
                if (
                    lastQuery.value.isOldQuery(
                        searchViewState.query,
                        searchViewState.keywordType
                    )
                ) return
                runSearch()
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorSearchScreenUiState.Content -> out.images.map { it.uri.uri.toUri() }
            else -> emptyList()
        }
    }

    fun onCreateUserAlbum() {
        if (loaderButtonState.isSuccess()) return
        loaderButtonState.loading()

        viewModelScope.launch {
            when (state.value) {
                is ExtractorSearchScreenUiState.Content -> {
                    compileUserAlbum(state.value.getImages())
                    _events.send(ExtractorSearchScreenEvents.AlbumCreated)
                    loaderButtonState.success()
                }

                else -> Unit
            }
        }
    }

    private suspend fun compileUserAlbum(input: List<Extraction>) {
        val newAlbum = NewAlbum(
            keyword = searchViewState.query,
            name = searchViewState.query,
            searchType = searchViewState.searchType,
            keywordType = searchViewState.keywordType,
            origin = NewAlbum.Origin.USER_GENERATED,
            entries = input.map {
                NewAlbum.Entry(
                    uri = it.uri,
                    id = it.mediaImageId
                )
            }
        )
        albumRepository.createAlbum(newAlbum)
    }

    private fun runSearch() {
        if (searchViewState.isBlank()) return
        if (_state.value is ExtractorSearchScreenUiState.StillIndexing) return

        _state.update { ExtractorSearchScreenUiState.Loading }

        viewModelScope.launch {
            val searchQuery = SearchImageByKeyword.Params(
                query = searchViewState.query,
                keywordType = searchViewState.keywordType,
                dateRange = dateFilterState.dateRange(),
                type = searchViewState.searchType
            )

            val result = imageSearch.execute(searchQuery).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
            }

            _state.createFrom(result)
        }
    }

    private data class LastQuery(val query: String, val keywordType: KeywordType)

    private fun LastQuery.isOldQuery(query: String, keywordType: KeywordType): Boolean {
        return when {
            (query == this.query) and (keywordType == this.keywordType) -> true
            else -> false
        }
    }

    fun performSuggestedSearch(suggestedSearch: SuggestedSearch) = with(suggestedSearch) {
        _state.update { ExtractorSearchScreenUiState.Loading }

        viewModelScope.launch {
            val searchQuery = SearchImageByKeyword.Params(
                query = query,
                keywordType = keywordType,
                dateRange = null,
                type = searchType
            )

            val result = imageSearch.execute(searchQuery).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
            }

            _state.createFrom(result)
            searchViewState.updateQuery(query)
        }
    }

    fun spawnWork() = spawnExtractorWork()

    fun onShowSheetDone() {
        viewModelScope.launch {
            datastore.hasSeenSearchSheet()
        }
    }

    fun resetSearch() {
        searchViewState.updateQuery("")
        dateFilterState.clearDates()
        _state.update {
            ExtractorSearchScreenUiState.ShowSuggestions(
                ExtractorSuggestedSearchState.Loading
            )
        }
        viewModelScope.launch {
            val suggested = generateSuggestedKeywords.invoke()
            _state.update {
                suggested.fold(
                    ifRight = { result ->
                        ExtractorSearchScreenUiState.ShowSuggestions(
                            ExtractorSuggestedSearchState.Content(result)
                        )
                    },
                    ifLeft = {
                        ExtractorSearchScreenUiState.NoSearchesLeft
                    }
                )
            }
        }
    }

    fun onSelectionClear() {
        gridState.clearSelection()
    }

    fun onSelectionCreate() {
        viewModelScope.launch {
            val extractions = state.value.getImages()

            val toCreate = gridState.checkedIndicesAsFlow()
                .first()
                .map { extractions[it] }

            compileUserAlbum(toCreate)
            gridState.clearSelection()
            //Show snack bar with action to view
            viewModelScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = stringProvider.get(R.string.snack_album_created),
                    actionLabel = stringProvider.get(R.string.snack_view)
                )
                when (result) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
                }
            }
        }
    }

    fun getSelectedImageUris(): List<Uri> {
        return gridState.checkedIndices().map { state.value.getImages()[it].uri.toUri() }
    }
}

