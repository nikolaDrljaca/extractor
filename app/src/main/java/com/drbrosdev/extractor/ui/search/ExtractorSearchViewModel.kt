package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.SearchImages
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetEvents
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.panic
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val _searchTrigger = MutableStateFlow<SearchImageByKeyword.Params?>(null)
    private val _suggestionTrigger = MutableStateFlow(Unit.toOption())

    private val _progress = trackExtractionProgress.invoke()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val searchCount = datastore.searchCount
        .onEach {
            if (it == 0) searchSheetState.disable()
            else searchSheetState.enable()
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            0
        )

    private val _containerState = combine(
        _searchTrigger,
        _suggestionTrigger,
        _progress,
    ) { search, suggestion, progress ->
        when (progress) {
            is ExtractionStatus.Running -> ExtractorSearchContainerState.StillIndexing
            is ExtractionStatus.Done -> {
                when (val searchParams = search.toOption()) {
                    None -> handleShowSuggestionState(suggestion)

                    is Some -> handleImageSearchState(searchParams.value)
                }
            }
        }
    }
        .combine(searchCount) { state, count ->
            when {
                count == 0 -> ExtractorSearchContainerState.NoSearchesLeft(
                    onGetMore = ::getMoreSearches
                )

                else -> state
            }
        }


    private val _isContainerStateLoading = MutableStateFlow(false)

    val containerState = combine(
        _isContainerStateLoading,
        _containerState
    ) { isLoading, containerState ->
        when {
            isLoading -> ExtractorSearchContainerState.Loading
            else -> containerState
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            ExtractorSearchContainerState.ShowSuggestions(
                ExtractorSuggestedSearchState.Loading
            )
        )

    val statusButtonState = _progress
        .map {
            when (it) {
                is ExtractionStatus.Done -> when {
                    it.isDataIncomplete -> ExtractorStatusButtonState.OutOfSync
                    else -> ExtractorStatusButtonState.Idle
                }

                is ExtractionStatus.Running -> ExtractorStatusButtonState.ExtractionRunning(
                    it.percentage
                )
            }
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorStatusButtonState.Idle
        )

    val searchSheetState = ExtractorSearchSheetState(
        stateHandle = stateHandle,
        eventHandler = ::processSearchSheetEvents
    )

    val snackbarHostState = SnackbarHostState()


    private val _events = Channel<ExtractorSearchScreenEvents>()
    val events = _events.receiveAsFlow()

    private val shouldShowSheetFlowJob = datastore.shouldShowSearchSheet
        .filter { it }
        .onEach { _events.send(ExtractorSearchScreenEvents.ShowSearchSheet) }
        .launchIn(viewModelScope)

    fun onShowSheetDone() {
        viewModelScope.launch {
            datastore.hasSeenSearchSheet()
        }
    }

    fun onSelectionClear() {
        when (val value = containerState.value) {
            is ExtractorSearchContainerState.Content -> value.gridState.clearSelection()

            else -> Unit
        }
    }

    fun onSelectionCreate() {
        when (val value = containerState.value) {
            is ExtractorSearchContainerState.Content -> {
                val extractions = value.getImages()
                val toCreate = value.gridState.checkedIndices()
                    .map { extractions[it] }

                viewModelScope.launch {
                    compileUserAlbum(toCreate)
                    value.gridState.clearSelection()

                    val result = snackbarHostState.showSnackbar(
                        message = stringProvider.get(R.string.snack_album_created),
                        actionLabel = stringProvider.get(R.string.snack_view),
                        duration = SnackbarDuration.Long
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
                    }
                }
            }

            else -> Unit
        }
    }

    fun getSelectedImageUris(): List<Uri> {
        return when (val value = containerState.value) {
            is ExtractorSearchContainerState.Content -> value.gridState.checkedIndices()
                .map { value.getImages()[it].uri.toUri() }

            else -> panic("Accessing checked items outside of content state.")
        }
    }

    fun createUserAlbum() {
        viewModelScope.launch {
            val extractions = containerState.value.getImages()
            compileUserAlbum(extractions)

            val result = snackbarHostState.showSnackbar(
                message = stringProvider.get(R.string.snack_album_created),
                actionLabel = stringProvider.get(R.string.snack_view),
                duration = SnackbarDuration.Long
            )
            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
            }
        }
    }

    private suspend fun handleImageSearchState(searchParams: SearchImageByKeyword.Params): ExtractorSearchContainerState {
        return imageSearch.execute(searchParams).fold(
            ifLeft = {
                ExtractorSearchContainerState.NoSearchesLeft(
                    onGetMore = ::getMoreSearches
                )
            },
            ifRight = {
                _isContainerStateLoading.update { false }
                when {
                    it.isEmpty() -> ExtractorSearchContainerState.Empty(
                        onReset = {
                            onSearchContainerContentEvent(
                                ExtractorSearchContainerEvents.OnReset
                            )
                        }
                    )

                    else -> ExtractorSearchContainerState.Content(
                        images = it,
                        eventHandler = ::onSearchContainerContentEvent
                    )
                }
            }
        )
    }

    private suspend fun handleShowSuggestionState(suggestion: Option<Unit>): ExtractorSearchContainerState {
        return when (suggestion) {
            None -> panic("Invalid state, suggestion trigger has to be something.")
            is Some -> {
                generateSuggestedKeywords.invoke().fold(
                    ifLeft = {
                        ExtractorSearchContainerState.NoSearchesLeft(
                            onGetMore = ::getMoreSearches
                        )
                    },
                    ifRight = {
                        _isContainerStateLoading.update { false }
                        when {
                            it.isEmpty() -> ExtractorSearchContainerState.ShowSuggestions(
                                ExtractorSuggestedSearchState.Empty(
                                    onStartSync = { spawnExtractorWork.invoke() }
                                )
                            )

                            else -> ExtractorSearchContainerState.ShowSuggestions(
                                ExtractorSuggestedSearchState.Content(
                                    onSuggestionClick = ::imageSearchWithSuggestion,
                                    suggestedSearches = it
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    private fun processSearchSheetEvents(event: ExtractorSearchSheetEvents) {
        val params = when (event) {
            is ExtractorSearchSheetEvents.OnChange -> with(event.data) {
                SearchImageByKeyword.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )
            }

            is ExtractorSearchSheetEvents.OnDateChange -> with(event.data) {
                SearchImageByKeyword.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )
            }

            is ExtractorSearchSheetEvents.OnSearchClick -> with(event.data) {
                viewModelScope.launch {
                    _events.send(ExtractorSearchScreenEvents.HideKeyboard)
                }

                SearchImageByKeyword.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )
            }
        }

        //don't perform a search with no query
        if (params.query.isBlank()) return

        _searchTrigger.update { params }
        _isContainerStateLoading.update { true }
    }

    private fun getMoreSearches() {
        viewModelScope.launch {
            _events.send(ExtractorSearchScreenEvents.NavToGetMore)
        }
    }

    private fun imageSearchWithSuggestion(suggestedSearch: SuggestedSearch) {
        val params = SearchImageByKeyword.Params(
            query = suggestedSearch.query,
            keywordType = suggestedSearch.keywordType,
            type = suggestedSearch.searchType,
            dateRange = null
        )
        _searchTrigger.update { params }
        _isContainerStateLoading.update { true }
        with(searchSheetState.searchViewState) {
            updateQuery(suggestedSearch.query)
            updateSearchType(suggestedSearch.searchType)
            updateKeywordType(suggestedSearch.keywordType)
        }
    }

    private fun onSearchContainerContentEvent(event: ExtractorSearchContainerEvents) {
        when (event) {
            is ExtractorSearchContainerEvents.OnImageClick -> {
                viewModelScope.launch {
                    _events.send(
                        ExtractorSearchScreenEvents.NavToImage(
                            imageUris = containerState.value.getImagesUris(),
                            initialIndex = event.index
                        )
                    )
                }
            }

            ExtractorSearchContainerEvents.OnReset -> {
                with(searchSheetState.searchViewState) {
                    updateSearchType(SearchType.PARTIAL)
                    updateKeywordType(KeywordType.ALL)
                    updateQuery("")
                }
                _searchTrigger.update { null }
                _isContainerStateLoading.update { true }
            }
        }
    }

    private suspend fun compileUserAlbum(input: List<Extraction>) {
        val newAlbum = NewAlbum(
            keyword = searchSheetState.searchViewState.query,
            name = searchSheetState.searchViewState.query,
            searchType = searchSheetState.searchViewState.searchType,
            keywordType = searchSheetState.searchViewState.keywordType,
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
}

