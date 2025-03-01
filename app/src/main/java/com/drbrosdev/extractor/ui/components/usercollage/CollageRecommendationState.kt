package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState2
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys


// loading and content
sealed class CollageRecommendationState {
    val gridState: ExtractorGridState2<MediaImageId> = ExtractorGridState2()

    val multiselectState by derivedStateOf {
        gridState.checkedKeys().isNotEmpty()
    }

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