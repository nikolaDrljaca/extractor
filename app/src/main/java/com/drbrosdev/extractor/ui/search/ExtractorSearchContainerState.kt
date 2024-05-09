package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.searchsheet.SheetContent
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import com.drbrosdev.extractor.util.panic
import com.drbrosdev.extractor.util.toUri

sealed interface ExtractorSearchContainerEvents {

    data object OnReset : ExtractorSearchContainerEvents

    data class OnImageClick(val index: Int) : ExtractorSearchContainerEvents

    data class OnCreateAlbumClick(val images: List<Extraction>): ExtractorSearchContainerEvents
}

sealed interface ExtractorSearchContainerState {

    @Stable
    data class Content(
        val images: List<Extraction>,
        val gridState: ExtractorGridState = ExtractorGridState(),
        val eventSink: (ExtractorSearchContainerEvents) -> Unit
    ) : ExtractorSearchContainerState {
        val topAppBarState = derivedStateOf {
            if (gridState.lazyGridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }

        val sheetContent = derivedStateOf {
            val checked = gridState.checkedIndices()
            when {
                (checked.isNotEmpty()) -> SheetContent.MultiselectBar
                else -> SheetContent.SearchView
            }
        }
    }

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
        else -> panic("Accessing image list outside of Success state.")
    }
}

fun ExtractorSearchContainerState.getImagesUris(): List<Uri> {
    return when (this) {
        is ExtractorSearchContainerState.Content -> this.images.map { it.uri.toUri() }
        else -> panic("Accessing image list outside of Success state.")
    }
}
