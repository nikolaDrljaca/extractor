package com.drbrosdev.extractor.ui.components.recommendsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.LupaBundle
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.util.panic

sealed class RecommendedSearchesState {
    data class SyncInProgress(
        val mostRecentLupaImage: LupaImageHighlight
    ) : RecommendedSearchesState()

    data object Loading : RecommendedSearchesState()

    data object Empty : RecommendedSearchesState()

    @Immutable
    data class Content(
        val items: List<LupaBundle>,
        val onImageClick: (keyword: String, index: Int) -> Unit
    ) : RecommendedSearchesState()
}

@Immutable
data class LupaImageHighlight(
    val lupaImageMetadata: LupaImageMetadata,
    val textEmbed: String,
    val visualEmbeds: List<String>
)

fun LupaImage.asHighlight() = LupaImageHighlight(
    lupaImageMetadata = metadata,
    textEmbed = annotations.textEmbed,
    visualEmbeds = annotations.visualEmbeds
)

fun RecommendedSearchesState.isShowcaseActive() =
    this is RecommendedSearchesState.SyncInProgress

fun RecommendedSearchesState.findCollageByKeyword(keyword: String) =
    when (this) {
        is RecommendedSearchesState.Content -> items
            .find { it.keyword.lowercase() == keyword.lowercase() }

        else -> panic("Accessing Collage items outside of content state.")
    }

fun RecommendedSearchesState.getImageUris(): Map<MediaImageId, LupaImageMetadata> =
    when (this) {
        is RecommendedSearchesState.Content -> items.flatMap { it.images }
            .associateBy { it.mediaImageId }

        else -> panic("Accessing Collage items outside of content state.")
    }

fun RecommendedSearchesState.keywords(): Set<String> = when (this) {
    is RecommendedSearchesState.Content -> items.map { it.keyword }
        .toSet()

    else -> panic("Accessing collage items outside of content state.")
}