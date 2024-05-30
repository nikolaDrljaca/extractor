package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.model.asAlbumName
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.SearchImages
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestionsError
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetEvents
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetState
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
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

    val searchSheetState = ExtractorSearchSheetState(
        stateHandle = stateHandle,
        eventHandler = ::searchSheetEventHandler
    )

    val snackbarHostState = SnackbarHostState()

    private val _searchTrigger = MutableSharedFlow<SearchTrigger>()
    private val _searchTriggerResult = _searchTrigger
        .flatMapLatest { searchTriggerProcessFlow(it) }

    private val _progress = trackExtractionProgress.invoke()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val searchCount = datastore.searchCount
        .onStart { _searchTrigger.emit(SearchTrigger.GenerateSuggestions) }
        .onEach {
            if (it == 0) searchSheetState.disable()
            else searchSheetState.enable()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(3000L),
            0
        )

    val containerState = combine(
        _searchTriggerResult,
        _progress,
    ) { search, progress ->
        when (progress) {
            is ExtractionStatus.Running -> ExtractorSearchContainerState.StillIndexing
            is ExtractionStatus.Done -> search
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

    fun multiselectEventHandler(event: MultiselectAction) {
        when (event) {
            MultiselectAction.Cancel -> when (val value = containerState.value) {
                is ExtractorSearchContainerState.Content -> value.gridState.clearSelection()
                else -> Unit
            }

            MultiselectAction.CreateAlbum -> when (val value = containerState.value) {
                is ExtractorSearchContainerState.Content -> viewModelScope.launch {
                    val extractions = value.getImages()
                    val toCreate = value.gridState.checkedIndices()
                        .map { extractions[it] }

                    compileUserAlbum(toCreate)
                    value.gridState.clearSelection()

                    val result = snackbarHostState.showSnackbar(
                        message = stringProvider.get(R.string.snack_album_created),
                        actionLabel = stringProvider.get(R.string.snack_view),
                        duration = SnackbarDuration.Short
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
                    }
                }

                else -> Unit
            }

            MultiselectAction.Share -> when (val value = containerState.value) {
                is ExtractorSearchContainerState.Content -> viewModelScope.launch {
                    val uris = value.gridState.checkedIndices()
                        .map { value.getImages()[it].uri.toUri() }
                    _events.send(ExtractorSearchScreenEvents.ShareSelectedImages(uris))
                }

                else -> Unit
            }

            MultiselectAction.Delete -> Unit // Unsupported for this grid
        }
    }

    private fun searchSheetEventHandler(event: ExtractorSearchSheetEvents) {
        when (event) {
            is ExtractorSearchSheetEvents.OnChange -> with(event.data) {
                val params = SearchImageByQuery.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )
                // don't perform search if the query is blank
                if (params.query.isBlank()) return
                viewModelScope.launch {
                    _searchTrigger.emit(SearchTrigger.Search(params))
                }
            }

            is ExtractorSearchSheetEvents.OnDateChange -> with(event.data) {
                val params = SearchImageByQuery.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )
                if (params.dateRange == null) return
                viewModelScope.launch {
                    _searchTrigger.emit(SearchTrigger.Search(params))
                }
            }

            is ExtractorSearchSheetEvents.OnSearchClick -> with(event.data) {
                viewModelScope.launch {
                    _events.send(ExtractorSearchScreenEvents.HideKeyboard)
                }

                val params = SearchImageByQuery.Params(
                    dateRange = dateRange,
                    query = query,
                    type = searchType,
                    keywordType = keywordType
                )

                viewModelScope.launch {
                    _searchTrigger.emit(SearchTrigger.Search(params))
                }
            }
        }
    }

    private fun searchContainerEventHandler(event: ExtractorSearchContainerEvents) {
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
                searchSheetState.dateFilterState.clearDates()
                viewModelScope.launch {
                    _searchTrigger.emit(SearchTrigger.GenerateSuggestions)
                }
            }

            is ExtractorSearchContainerEvents.OnCreateAlbumClick ->
                viewModelScope.launch {
                    compileUserAlbum(event.images)

                    val result = snackbarHostState.showSnackbar(
                        message = stringProvider.get(R.string.snack_album_created),
                        actionLabel = stringProvider.get(R.string.snack_view),
                        duration = SnackbarDuration.Short
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> _events.send(ExtractorSearchScreenEvents.AlbumCreated)
                    }
                }
        }
    }

    private fun getMoreSearches() {
        viewModelScope.launch {
            _events.send(ExtractorSearchScreenEvents.NavToGetMore)
        }
    }

    private fun performImageSearchUsingSuggestion(suggestedSearch: SuggestedSearch) {
        val params = SearchImageByQuery.Params(
            query = suggestedSearch.query,
            keywordType = suggestedSearch.keywordType,
            type = suggestedSearch.searchType,
            dateRange = null
        )
        with(searchSheetState.searchViewState) {
            updateQuery(suggestedSearch.query)
            updateSearchType(suggestedSearch.searchType)
            updateKeywordType(suggestedSearch.keywordType)
        }

        viewModelScope.launch {
            _searchTrigger.emit(SearchTrigger.Search(params))
        }
    }

    private fun searchTriggerProcessFlow(searchTrigger: SearchTrigger) = flow {
        when (searchTrigger) {
            SearchTrigger.GenerateSuggestions -> {
                emit(
                    ExtractorSearchContainerState.ShowSuggestions(
                        ExtractorSuggestedSearchState.Loading
                    )
                )
                emit(generateSuggestions())
            }

            is SearchTrigger.Search -> {
                emit(ExtractorSearchContainerState.Loading)
                emit(runImageSearch(searchTrigger.params))
            }

            SearchTrigger.Noop -> emit(ExtractorSearchContainerState.StillIndexing)
        }
    }

    private suspend fun runImageSearch(
        searchParams: SearchImageByQuery.Params
    ): ExtractorSearchContainerState {
        return imageSearch.execute(searchParams).fold(
            ifLeft = {
                ExtractorSearchContainerState.NoSearchesLeft(
                    onGetMore = ::getMoreSearches
                )
            },
            ifRight = {
                when {
                    it.isEmpty() -> ExtractorSearchContainerState.Empty(
                        onReset = {
                            searchContainerEventHandler(
                                ExtractorSearchContainerEvents.OnReset
                            )
                        }
                    )

                    else -> ExtractorSearchContainerState.Content(
                        images = it,
                        eventSink = ::searchContainerEventHandler
                    )
                }
            }
        )
    }

    private suspend fun generateSuggestions(): ExtractorSearchContainerState {
        return generateSuggestedKeywords.invoke().fold(
            ifLeft = {
                when (it) {
                    GenerateSuggestionsError.NoExtractionsPresent ->
                        ExtractorSearchContainerState.ShowSuggestions(
                            ExtractorSuggestedSearchState.Empty(
                                onStartSync = { spawnExtractorWork.invoke() }
                            )
                        )

                    GenerateSuggestionsError.NoSearchesLeft ->
                        ExtractorSearchContainerState
                            .NoSearchesLeft(
                                onGetMore = ::getMoreSearches
                            )
                }
            },
            ifRight = {
                when {
                    it.isEmpty() -> ExtractorSearchContainerState.ShowSuggestions(
                        ExtractorSuggestedSearchState.Empty(
                            onStartSync = { spawnExtractorWork.invoke() }
                        )
                    )

                    else -> ExtractorSearchContainerState.ShowSuggestions(
                        ExtractorSuggestedSearchState.Content(
                            onSuggestionClick = ::performImageSearchUsingSuggestion,
                            suggestedSearches = it
                        )
                    )
                }
            }
        )
    }

    private suspend fun compileUserAlbum(input: List<Extraction>) {
        // decide album name based on query type -> Text or Date
        val albumName = with(searchSheetState) {
            val dateRange = dateFilterState.dateRange()

            when {
                searchViewState.query.isNotBlank() -> searchViewState.query
                dateRange != null -> dateRange.asAlbumName()
                else -> ""
            }
        }

        val newAlbum = NewAlbum(
            name = albumName,
            keyword = searchSheetState.searchViewState.query,
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


private sealed interface SearchTrigger {

    data object Noop : SearchTrigger

    data object GenerateSuggestions : SearchTrigger

    data class Search(
        val params: SearchImageByQuery.Params
    ) : SearchTrigger
}
