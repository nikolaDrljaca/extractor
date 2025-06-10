package com.drbrosdev.extractor.ui.overview

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.ExtractionProgress
import com.drbrosdev.extractor.domain.model.isDataIncomplete
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.generate.GenerateMostCommonExtractionBundles
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.framework.FeatureFlags
import com.drbrosdev.extractor.framework.check
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesComponent
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchComponent
import com.drbrosdev.extractor.ui.yourspace.ExtractorYourSpaceNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.shop.ExtractorHubNavTarget
import com.drbrosdev.extractor.util.WhileUiSubscribed
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    val statusPillState = combine(
        trackExtractionProgress.invoke(),
        dataStore.searchCount
    ) { progress, count ->
        when (progress) {
            is ExtractionProgress.Running -> ExtractorStatusPillState.SyncInProgress(
                progress = progress.percentage
            )

            is ExtractionProgress.Done -> {
                when {
                    progress.isDataIncomplete() -> ExtractorStatusPillState.OutOfSync

                    FeatureFlags.SEARCH_COUNT_ENABLED.check().not() ->
                        ExtractorStatusPillState.Disabled(progress.inStorageCount)

                    else -> ExtractorStatusPillState.Idle(searchesLeft = count)
                }
            }
        }
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorStatusPillState.Idle(0)
        )

    val suggestedSearchComponent = SuggestedSearchComponent(
        coroutineScope = viewModelScope,
        compileSearchSuggestions = compileSearchSuggestions,
        extractionProgress = trackExtractionProgress.invoke(),
        navigators = navigators
    )

    val recommendedSearchesComponent = RecommendedSearchesComponent(
        coroutineScope = viewModelScope,
        extractionProgress = trackExtractionProgress.invoke(),
        generateBundles = generateMostCommonExtractionBundles,
        createAlbum = albumRepository::createAlbum,
        mostRecentExtractionFlow = extractorRepository.getLatestExtractionAsFlow(),
        navigators = navigators
    )

    // snackbars should be handled by screen view model
    val snackbarHostState = SnackbarHostState()

    fun onHomeClick() {
        navigators.navController.navigate(ExtractorYourSpaceNavTarget)
    }

    fun onHubClick() {
        navigators.navController.navigate(ExtractorHubNavTarget)
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
                    navigators.navController.navigate(ExtractorYourSpaceNavTarget)
            }
        }

    fun onSearchClick() {
        navigators.navController.navigate(ExtractorSearchNavTarget())
    }
}