package com.drbrosdev.extractor.ui.overview

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.toOption
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

sealed interface OverviewContentState {
    data object Done : OverviewContentState

    data object Idle : OverviewContentState

    data class SyncInProgress(
        val mostRecentExtraction: ExtractionData
    ) : OverviewContentState
}

class ExtractorOverviewViewModel(
    private val trackExtractionProgress: TrackExtractionProgress,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val compileMostCommonTextEmbeds: CompileMostCommonTextEmbeds,
    private val compileMostCommonVisualEmbeds: CompileMostCommonVisualEmbeds,
    private val dataStore: ExtractorDataStore,
    private val albumRepository: AlbumRepository,
    private val extractorRepository: ExtractorRepository,
    private val navigators: Navigators
) : ViewModel() {

    private val upstreamProgress = trackExtractionProgress.invoke()
        .map {
            when (it) {
                is ExtractionStatus.Done -> OverviewContentState.Done
                is ExtractionStatus.Running -> extractorRepository.getLatestExtraction()
                    .toOption()
                    .fold(
                        ifEmpty = { OverviewContentState.Idle },
                        ifSome = { data ->
                            println(data.visualEmbeds)
                            OverviewContentState.SyncInProgress(data)
                        }
                    )
            }
        }
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(3_500)
        }
    }

    // zip with a ticker flow to make emissions from progress tracking flow at a constant rate
    val overviewContentState = upstreamProgress.zip(ticker) { value, _ -> value }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            OverviewContentState.Idle
        )

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