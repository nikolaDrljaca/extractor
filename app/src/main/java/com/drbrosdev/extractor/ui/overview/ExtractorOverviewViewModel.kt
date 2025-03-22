package com.drbrosdev.extractor.ui.overview

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.generate.CompileMostCommonTextEmbeds
import com.drbrosdev.extractor.domain.usecase.generate.CompileMostCommonVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesComponent
import com.drbrosdev.extractor.ui.components.statuspill.StatusPillComponent
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchComponent
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.shop.ExtractorShopNavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.launch

class ExtractorOverviewViewModel(
    private val trackExtractionProgress: TrackExtractionProgress,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val compileMostCommonTextEmbeds: CompileMostCommonTextEmbeds,
    private val compileMostCommonVisualEmbeds: CompileMostCommonVisualEmbeds,
    private val dataStore: ExtractorDataStore,
    private val albumRepository: AlbumRepository,
    private val navigators: Navigators
) : ViewModel() {

    val statusPillComponent = StatusPillComponent(
        coroutineScope = viewModelScope,
        trackProgress = trackExtractionProgress,
        dataStore = dataStore
    )

    val suggestedSearchComponent = SuggestedSearchComponent(
        coroutineScope = viewModelScope,
        compileSearchSuggestions = compileSearchSuggestions,
        trackExtractionProgress = trackExtractionProgress,
        navigators = navigators
    )

    val recommendedSearchesComponent = RecommendedSearchesComponent(
        coroutineScope = viewModelScope,
        compileMostCommonTextEmbeds = compileMostCommonTextEmbeds,
        compileMostCommonVisualEmbeds = compileMostCommonVisualEmbeds,
        createAlbum = albumRepository::createAlbum,
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

    fun showAlbumCreatedSnack(message: String, actionLabel: String) = viewModelScope.launch {
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