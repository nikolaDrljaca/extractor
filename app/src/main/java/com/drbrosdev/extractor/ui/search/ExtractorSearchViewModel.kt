package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.SearchImages
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndicesAsFlow
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class ExtractorSearchViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val imageSearch: SearchImages,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateSuggestedKeywords: GenerateSuggestedKeywords,
    private val spawnExtractorWork: SpawnExtractorWork,
    private val datastore: ExtractorDataStore,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    val searchSheetState = ExtractorSearchSheetState.fromSavedStateHandle(stateHandle)

    val snackbarHostState = SnackbarHostState()
    val extractorStatusButtonState = ExtractorStatusButtonState()

    val gridState = ExtractorGridState()

    private val _state = MutableStateFlow<ExtractorSearchContainerState>(
        ExtractorSearchContainerState.ShowSuggestions(
            ExtractorSuggestedSearchState.Loading
        )
    )
    val state = _state.asStateFlow()

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

//    private val progressJob = trackExtractionProgress.invoke()
//        .distinctUntilChanged()
//        //handle create album button state
//        .onEach {
//            val status = when (it) {
//                is ExtractionStatus.Done -> when {
//                    it.isDataIncomplete -> ExtractorStatusButtonState.Status.OutOfSync
//                    else -> ExtractorStatusButtonState.Status.Idle
//                }
//
//                is ExtractionStatus.Running -> ExtractorStatusButtonState.Status.ExtractionRunning(
//                    it.percentage
//                )
//            }
//            extractorStatusButtonState.update(status)
//        }
//        //handle show suggestions
//        .onEach {
//            when (it) {
//                is ExtractionStatus.Done -> {
//                    when {
//                        ((it.inStorageCount != 0) and (_state.value !is ExtractorSearchScreenUiState.Content)) -> {
//                            val suggested = generateSuggestedKeywords.invoke()
//                            _state.update {
//                                suggested.fold(
//                                    ifRight = { result ->
//                                        ExtractorSearchScreenUiState.ShowSuggestions(
//                                            ExtractorSuggestedSearchState.Content(result)
//                                        )
//                                    },
//                                    ifLeft = {
//                                        ExtractorSearchScreenUiState.NoSearchesLeft
//                                    }
//                                )
//                            }
//                        }
//
//                        else -> {
//                            _state.update {
//                                ExtractorSearchScreenUiState.ShowSuggestions(
//                                    ExtractorSuggestedSearchState.Empty
//                                )
//                            }
//                        }
//                    }
//                }
//
//                is ExtractionStatus.Running -> _state.update {
//                    ExtractorSearchScreenUiState.StillIndexing
//                }
//            }
//
//        }
//        .launchIn(viewModelScope)
//
//    private val labelTypeUpdateFlow = searchViewState.keywordTypeAsFlow()
//        .distinctUntilChanged()
//        .onEach { performSearch(SearchStrategy.NORMAL) }
//        .onEach { loaderButtonState.initial() }
//        .launchIn(viewModelScope)
//
//    private val searchTypeUpdateFlow = searchViewState.searchTypeAsFlow()
//        .distinctUntilChanged()
//        .onEach { performSearch(SearchStrategy.NORMAL) }
//        .onEach { loaderButtonState.initial() }
//        .launchIn(viewModelScope)
//
//    private val saveQueryJob = searchViewState.queryAsFlow()
//        .distinctUntilChanged()
//        .onEach { loaderButtonState.initial() }
//        .launchIn(viewModelScope)
//
//    private val dateFilterJob = dateFilterState.dateRangeAsFlow()
//        .distinctUntilChanged()
//        .onEach { performSearch(SearchStrategy.NORMAL) }
//        .launchIn(viewModelScope)
//
//    private val disableSearchJob = datastore.searchCount
//        .distinctUntilChanged()
//        .onEach {
//            when {
//                it == 0 -> searchSheetState.disable()
//                else -> searchSheetState.enable()
//            }
//        }
//        .launchIn(viewModelScope)
//
//    fun performSearch(searchStrategy: SearchStrategy) {
//        runSearch()
//        when (searchStrategy) {
//            SearchStrategy.NORMAL -> runSearch()
//            SearchStrategy.DIRTY_CHECKING -> {
//                if (searchViewState.isBlank()) return
//                if (
//                    lastQuery.value.isOldQuery(
//                        searchViewState.query,
//                        searchViewState.keywordType
//                    )
//                ) return
//                runSearch()
//            }
//        }
//    }
//
//    fun getImageUris(): List<Uri> {
//        return when (val out = state.value) {
//            is ExtractorSearchScreenUiState.Content -> out.images.map { it.uri.uri.toUri() }
//            else -> emptyList()
//        }
//    }
//
//    fun onCreateUserAlbum() {
//        if (loaderButtonState.isSuccess()) return
//        loaderButtonState.loading()
//
//        viewModelScope.launch {
//            when (state.value) {
//                is ExtractorSearchScreenUiState.Content -> {
//                    compileUserAlbum(state.value.getImages())
//                    _events.send(ExtractorSearchScreenEvents.AlbumCreated)
//                    loaderButtonState.success()
//                }
//
//                else -> Unit
//            }
//        }
//    }
//
//    private suspend fun compileUserAlbum(input: List<Extraction>) {
//        val newAlbum = NewAlbum(
//            keyword = searchViewState.query,
//            name = searchViewState.query,
//            searchType = searchViewState.searchType,
//            keywordType = searchViewState.keywordType,
//            origin = NewAlbum.Origin.USER_GENERATED,
//            entries = input.map {
//                NewAlbum.Entry(
//                    uri = it.uri,
//                    id = it.mediaImageId
//                )
//            }
//        )
//        albumRepository.createAlbum(newAlbum)
//    }
//
//    private fun runSearch() {
//        if (searchViewState.isBlank()) return
//        if (_state.value is ExtractorSearchScreenUiState.StillIndexing) return
//
//        _state.update { ExtractorSearchScreenUiState.Loading }
//
//        viewModelScope.launch {
//            val searchQuery = SearchImageByKeyword.Params(
//                query = searchViewState.query,
//                keywordType = searchViewState.keywordType,
//                dateRange = dateFilterState.dateRange(),
//                type = searchViewState.searchType
//            )
//
//            val result = imageSearch.execute(searchQuery).also {
//                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
//            }
//
//            _state.createFrom(result)
//        }
//    }
//
//    private data class LastQuery(val query: String, val keywordType: KeywordType)
//
//    private fun LastQuery.isOldQuery(query: String, keywordType: KeywordType): Boolean {
//        return when {
//            (query == this.query) and (keywordType == this.keywordType) -> true
//            else -> false
//        }
//    }
//
//    fun performSuggestedSearch(suggestedSearch: SuggestedSearch) = with(suggestedSearch) {
//        _state.update { ExtractorSearchScreenUiState.Loading }
//
//        viewModelScope.launch {
//            val searchQuery = SearchImageByKeyword.Params(
//                query = query,
//                keywordType = keywordType,
//                dateRange = null,
//                type = searchType
//            )
//
//            val result = imageSearch.execute(searchQuery).also {
//                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
//            }
//
//            _state.createFrom(result)
//            searchViewState.updateQuery(query)
//        }
//    }
//
//    fun spawnWork() = spawnExtractorWork.invoke()
//
//    fun onShowSheetDone() {
//        viewModelScope.launch {
//            datastore.hasSeenSearchSheet()
//        }
//    }
//
//    fun resetSearch() {
//        searchViewState.updateQuery("")
//        dateFilterState.clearDates()
//        _state.update {
//            ExtractorSearchScreenUiState.ShowSuggestions(
//                ExtractorSuggestedSearchState.Loading
//            )
//        }
//        viewModelScope.launch {
//            val suggested = generateSuggestedKeywords.invoke()
//            _state.update {
//                suggested.fold(
//                    ifRight = { result ->
//                        ExtractorSearchScreenUiState.ShowSuggestions(
//                            ExtractorSuggestedSearchState.Content(result)
//                        )
//                    },
//                    ifLeft = {
//                        ExtractorSearchScreenUiState.NoSearchesLeft
//                    }
//                )
//            }
//        }
//    }
//
//    fun onSelectionClear() {
//        gridState.clearSelection()
//    }
//
//    fun onSelectionCreate() {
//        viewModelScope.launch {
//            val extractions = state.value.getImages()
//
//            val toCreate = gridState.checkedIndicesAsFlow()
//                .first()
//                .map { extractions[it] }
//
//            compileUserAlbum(toCreate)
//            gridState.clearSelection()
//            //Show snack bar with action to view
//            viewModelScope.launch {
//                val result = snackbarHostState.showSnackbar(
//                    message = stringProvider.get(R.string.snack_album_created),
//                    actionLabel = stringProvider.get(R.string.snack_view)
//                )
//                when (result) {
//                    SnackbarResult.Dismissed -> Unit
//                    SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
//                }
//            }
//        }
//    }
//
//    fun getSelectedImageUris(): List<Uri> {
//        return gridState.checkedIndices().map { state.value.getImages()[it].uri.toUri() }
//    }
}

