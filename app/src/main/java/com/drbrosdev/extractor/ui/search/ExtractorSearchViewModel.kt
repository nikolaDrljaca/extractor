package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.asAlbumName
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.SearchCountPositiveDelta
import com.drbrosdev.extractor.domain.usecase.image.SearchImages
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestedKeywords
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.framework.logger.logEvent
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponent
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.yourspace.ExtractorYourSpaceNavTarget
import com.drbrosdev.extractor.util.memo
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

class ExtractorSearchViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val imageSearch: SearchImages,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateSuggestedKeywords: GenerateSuggestedKeywords,
    private val searchCountPositiveDelta: SearchCountPositiveDelta,
    private val datastore: ExtractorDataStore,
    private val navigators: Navigators,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    val searchResultComponent = SearchResultComponent(
        coroutineScope = viewModelScope,
        navigators = navigators,
        searchImages = imageSearch::execute,
        createAlbum = ::compileUserAlbum
    )

    val searchSheetComponent = ExtractorSearchSheetComponent(
        stateHandle = stateHandle,
        onSearchEvent = ::performSearch
    )

    val snackbarHostState = SnackbarHostState()

    // memoize to run only once for one ImageSearchParams
    private val searchUsingArgs: (ImageSearchParams) -> Unit = memo {
        searchSheetComponent.query.setTextAndPlaceCursorAtEnd(it.query)
        searchSheetComponent.keywordType = it.keywordType
        searchSheetComponent.searchType = it.searchType
        searchResultComponent.executeSearch(it)
    }

    // memoize to run only once for a certain tag
    private val requestSearchFieldFocus = memo("requestFocus") {
        searchSheetComponent.requestFocus()
    }

    // Safe to call from side-effects -- memoized
    fun onInitWithSearchArgs(params: ImageSearchParams?) {
        if (params != null) {
            searchUsingArgs(params)
        } else {
            // Pretty bad -- should take a better approach
            viewModelScope.launch {
                delay(500L)
                requestSearchFieldFocus()
            }
        }
    }

    private fun performSearch(params: ImageSearchParams) {
        searchResultComponent.executeSearch(params)
    }

    private fun compileUserAlbum(input: List<Extraction>) {
        viewModelScope.launch {
            val searchData = searchSheetComponent.getSearchParamSnapshot()
            val name = when {
                searchData.query.isNotBlank() -> searchData.query
                searchData.dateRange != null -> searchData.dateRange.asAlbumName()
                else -> {
                    logEvent("ExtractorSearchViewModel.compileUserAlbum: Attempting to save an album with no name. Using timestamp.")
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                }
            }
            val albumEntries = input.map { NewAlbum.Entry(it.uri, it.mediaImageId) }
            val album = NewAlbum(
                name = name,
                keyword = searchData.query,
                searchType = searchData.searchType,
                keywordType = searchData.keywordType,
                origin = NewAlbum.Origin.USER_GENERATED,
                entries = albumEntries
            )
            albumRepository.createAlbum(album)
            val result = snackbarHostState.showSnackbar(
                message = stringProvider.get(R.string.album_created),
                actionLabel = stringProvider.get(R.string.snack_view)
            )
            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> navigators.navController.navigate(
                    ExtractorYourSpaceNavTarget
                )
            }
        }
    }
}
