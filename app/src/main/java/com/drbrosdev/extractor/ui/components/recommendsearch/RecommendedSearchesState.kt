package com.drbrosdev.extractor.ui.components.recommendsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionBundle
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.util.panic

sealed class RecommendedSearchesState {
    data class SyncInProgress(
        val mostRecentExtraction: ExtractionData
    ) : RecommendedSearchesState()

    data object Loading : RecommendedSearchesState()

    data object Empty : RecommendedSearchesState()

    @Immutable
    data class Content(
        val items: List<ExtractionBundle>,
        val onImageClick: (keyword: String, index: Int) -> Unit
    ) : RecommendedSearchesState()
}

fun RecommendedSearchesState.isShowcaseActive() =
    this is RecommendedSearchesState.SyncInProgress

fun RecommendedSearchesState.findCollageByKeyword(keyword: String) =
    when (this) {
        is RecommendedSearchesState.Content -> items
            .find { it.keyword.lowercase() == keyword.lowercase() }

        else -> panic("Accessing Collage items outside of content state.")
    }

fun RecommendedSearchesState.getImageUris(): Map<MediaImageId, Extraction> =
    when (this) {
        is RecommendedSearchesState.Content -> items.flatMap { it.extractions }
            .associateBy { it.mediaImageId }

        else -> panic("Accessing Collage items outside of content state.")
    }

fun RecommendedSearchesState.keywords(): Set<String> = when (this) {
    is RecommendedSearchesState.Content -> items.map { it.keyword }
        .toSet()

    else -> panic("Accessing collage items outside of content state.")
}