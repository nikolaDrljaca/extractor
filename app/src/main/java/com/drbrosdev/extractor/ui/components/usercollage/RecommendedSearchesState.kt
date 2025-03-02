package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.ui.dialog.status.safeDiv

// loading and content
sealed class RecommendedSearchesState {
    data object Loading : RecommendedSearchesState()

    data object Empty : RecommendedSearchesState()

    @Immutable
    data class Content(
        val items: List<ExtractionCollage>,
        val onImageClick: (keyword: String, index: Int) -> Unit
    ) : RecommendedSearchesState()

    @Immutable
    data class SyncInProgress(
        val progress: Int
    ) : RecommendedSearchesState() {
        val asFloat: Float = progress safeDiv 100
    }
}

fun RecommendedSearchesState.findCollageByKeyword(keyword: String) = when (this) {
    is RecommendedSearchesState.Content -> items
        .find { it.keyword.lowercase() == keyword.lowercase() }

    else -> error("Accessing Collage items outside of content state.")
}