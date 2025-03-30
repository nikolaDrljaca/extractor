package com.drbrosdev.extractor.ui.overview

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.generate.GenerateMostCommonExtractionBundles
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesComponent
import com.drbrosdev.extractor.ui.components.statuspill.StatusPillComponent
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchComponent
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.shop.ExtractorShopNavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class ExtractorOverviewViewModel(
    private val trackExtractionProgress: TrackExtractionProgress,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val generateMostCommonExtractionBundles: GenerateMostCommonExtractionBundles,
    private val dataStore: ExtractorDataStore,
    private val albumRepository: AlbumRepository,
    private val extractorRepository: ExtractorRepository,
    private val navigators: Navigators
) : ViewModel() {

    // collect extraction progress as sharedFlow to multicast
    private val extractionStatus = trackExtractionProgress.invoke()
        .shareIn(
            viewModelScope,
            SharingStarted.Lazily
        )

    val statusPillComponent = StatusPillComponent(
        coroutineScope = viewModelScope,
        extractionStatus = extractionStatus,
        dataStore = dataStore
    )

    val suggestedSearchComponent = SuggestedSearchComponent(
        coroutineScope = viewModelScope,
        compileSearchSuggestions = compileSearchSuggestions,
        extractionStatus = extractionStatus,
        navigators = navigators
    )

    val recommendedSearchesComponent = RecommendedSearchesComponent(
        coroutineScope = viewModelScope,
        extractionStatus = extractionStatus,
        generateBundles = generateMostCommonExtractionBundles,
        createAlbum = albumRepository::createAlbum,
        getMostRecentExtraction = extractorRepository::getLatestExtraction,
        navigators = navigators
    )

    // snackbars should be handled by screen view model
    val snackbarHostState = SnackbarHostState()

    fun onHomeClick() {
        navigators.navController.navigate(ExtractorHomeNavTarget)
    }

    fun onHubClick() {
        navigators.navController.navigate(ExtractorShopNavTarget)
    }

    fun showAlbumCreatedSnack(message: String, actionLabel: String) =
        viewModelScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed ->
                    navigators.navController.navigate(ExtractorHomeNavTarget)
            }
        }

    fun onSearchClick() {
        navigators.navController.navigate(ExtractorSearchNavTarget())
    }
}