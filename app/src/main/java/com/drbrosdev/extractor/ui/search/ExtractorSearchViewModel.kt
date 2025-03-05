package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.asAlbumName
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.SearchCountPositiveDelta
import com.drbrosdev.extractor.domain.usecase.image.SearchImages
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestedKeywords
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetEvent
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class ExtractorSearchViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val imageSearch: SearchImages,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateSuggestedKeywords: GenerateSuggestedKeywords,
    private val spawnExtractorWork: SpawnExtractorWork,
    private val searchCountPositiveDelta: SearchCountPositiveDelta,
    private val datastore: ExtractorDataStore,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    val searchSheetState = ExtractorSearchSheetComponent(
        stateHandle = stateHandle,
        eventHandler = ::searchSheetEventHandler
    )

    val searchResultComponent = SearchResultComponent(
        coroutineScope = viewModelScope,
        searchImages = imageSearch
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

    fun multiselectEventHandler(event: MultiselectAction) {
        when (event) {
            MultiselectAction.Delete -> Unit // Unsupported for this grid
            MultiselectAction.Cancel -> Unit
            MultiselectAction.CreateAlbum -> Unit
            MultiselectAction.Share -> Unit
        }
    }

    private fun searchSheetEventHandler(event: ExtractorSearchSheetEvent) {
        when (event) {
            is ExtractorSearchSheetEvent.OnChange -> {
                // don't perform search if the query is blank
                if (event.params.query.isBlank()) return
                searchResultComponent.executeSearch(event.params)
            }

            is ExtractorSearchSheetEvent.OnDateChange -> {
                if (event.params.dateRange == null) {
                    // reset result state
                    searchResultComponent.executeSearch(null)
                    return
                }
                searchResultComponent.executeSearch(event.params)
            }

            is ExtractorSearchSheetEvent.OnSearch -> {
                // don't perform search if the query is blank
                if (event.params.query.isBlank()) return
                // TODO state class to control keyboard -> hide
                // https://developer.android.com/reference/kotlin/androidx/compose/ui/platform/SoftwareKeyboardController
                searchResultComponent.executeSearch(event.params)
            }
        }
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
        val imageSearchParams: ImageSearchParams
    ) : SearchTrigger
}
