package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.runtime.Immutable

// loading and content
sealed class CollageRecommendationState {
    data object Loading : CollageRecommendationState()

    data object Empty : CollageRecommendationState()

    @Immutable
    data class Content(
        val items: List<ExtractionCollage>,
        val onImageClick: (keyword: String, index: Int) -> Unit
    ) : CollageRecommendationState()
}

fun CollageRecommendationState.findCollageByKeyword(keyword: String) = when (this) {
    is CollageRecommendationState.Content -> items
        .find { it.keyword.lowercase() == keyword.lowercase() }

    else -> error("Accessing Collage items outside of content state.")
}