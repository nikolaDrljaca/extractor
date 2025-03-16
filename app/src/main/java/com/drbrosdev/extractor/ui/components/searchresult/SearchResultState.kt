package com.drbrosdev.extractor.ui.components.searchresult

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.util.panic

@Immutable
sealed interface SearchResultState {
    data class NoSearchesLeft(
        val onGetMore: () -> Unit
    ) : SearchResultState

    // result list is empty
    data object Empty : SearchResultState

    // no search was executed yet -- initial state
    data object Idle : SearchResultState

    @Immutable
    data class Content(
        val images: List<Extraction>,
        val eventSink: (SearchResultContentEvents) -> Unit,
    ) : SearchResultState
}

fun SearchResultState.getImages(): List<Extraction> = when (this) {
    is SearchResultState.Content -> this.images
    else -> panic("SearchResultState: Accessing extractions outside of Content state!")
}

fun SearchResultState.getImageLookup(): Map<MediaImageId, Extraction> =
    getImages().associateBy { it.mediaImageId }

sealed interface SearchResultContentEvents {
    data class OnImageClick(val image: Extraction) : SearchResultContentEvents

    data class OnLongImageTap(val image: Extraction) : SearchResultContentEvents

    data class OnCreateAlbumClick(val images: List<Extraction>) : SearchResultContentEvents
}

