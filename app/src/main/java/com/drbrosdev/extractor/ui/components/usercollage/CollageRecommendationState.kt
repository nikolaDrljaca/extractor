package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.runtime.Immutable

// loading and content
sealed interface CollageRecommendationState {
    data object Loading : CollageRecommendationState

    data object Empty : CollageRecommendationState

    @Immutable
    data class Content(
        val items: List<ExtractionCollage>
    ) : CollageRecommendationState
}