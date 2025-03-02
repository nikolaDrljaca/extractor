package com.drbrosdev.extractor.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.statuspill.StatusPillComponent
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchComponent
import com.drbrosdev.extractor.ui.components.usercollage.RecommendedSearchesComponent
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.purchase.ExtractorPurchaseSearchNavTarget
import dev.olshevski.navigation.reimagined.navigate

class ExtractorOverviewViewModel(
    private val trackExtractionProgress: TrackExtractionProgress,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val compileTextAlbums: CompileTextAlbums,
    private val dataStore: ExtractorDataStore,
    private val generateUserCollage: GenerateUserCollage,
    private val navigators: Navigators
) : ViewModel() {
    val overviewGridState = OverviewGridState()

    val statusPillComponent = StatusPillComponent(
        coroutineScope = viewModelScope,
        trackProgress = trackExtractionProgress,
        dataStore = dataStore
    )

    val suggestedSearchComponent = SuggestedSearchComponent(
        coroutineScope = viewModelScope,
        compileSearchSuggestions = compileSearchSuggestions,
        trackExtractionProgress = trackExtractionProgress,
        onSearch = { suggestedSearch ->
            // navigate to search screen
        }
    )

    val collageRecommendationComponent = RecommendedSearchesComponent(
        coroutineScope = viewModelScope,
        generateUserCollage = generateUserCollage,
        compileTextAlbums = compileTextAlbums,
        trackExtractionProgress = trackExtractionProgress
    )

    fun onHomeClick() {
        navigators.navController.navigate(ExtractorHomeNavTarget)
    }

    fun onHubClick() {
        navigators.navController.navigate(ExtractorPurchaseSearchNavTarget)
    }

    fun multiselectBarEventHandler(event: MultiselectAction) = when (event) {
        MultiselectAction.Cancel -> Unit
        MultiselectAction.CreateAlbum -> Unit
        MultiselectAction.Delete -> Unit
        MultiselectAction.Share -> Unit
    }
}