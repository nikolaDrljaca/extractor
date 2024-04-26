package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState

sealed interface ExtractorSearchContainerEvents {
    data object OnReset : ExtractorSearchContainerEvents
    data class OnImageClick(val extraction: Extraction) : ExtractorSearchContainerEvents
}

sealed interface ExtractorSearchContainerState {

    @Immutable
    data class Content(
        val images: List<Extraction>,
        val gridState: ExtractorGridState = ExtractorGridState(),
        val eventHandler: (ExtractorSearchContainerEvents) -> Unit
    ) : ExtractorSearchContainerState

    data object Loading : ExtractorSearchContainerState

    data object StillIndexing : ExtractorSearchContainerState

    data class ShowSuggestions(
        val suggestedSearchState: ExtractorSuggestedSearchState,
    ) : ExtractorSearchContainerState

    data class NoSearchesLeft(
        val onGetMore: () -> Unit
    ) : ExtractorSearchContainerState

    data class Empty(
        val onReset: () -> Unit
    ) : ExtractorSearchContainerState
}

fun ExtractorSearchContainerState.getImages(): List<Extraction> {
    return when (this) {
        is ExtractorSearchContainerState.Content -> this.images
        else -> error("Accessing image list outside of Success state.")
    }
}

sealed interface SheetContent {
    data object SearchView : SheetContent

    data object MultiselectBar : SheetContent
}