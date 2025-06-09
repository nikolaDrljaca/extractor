package com.drbrosdev.extractor.ui.albumviewer

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.util.panic

sealed interface ExtractorAlbumViewerScreenState {

    @Immutable
    data class Content(
        val album: Album,
        val dialogSelection: ExtractorAlbumDialogSelection,
        val shouldShowSelectBar: Boolean = false
    ) : ExtractorAlbumViewerScreenState {
        val metadata =
            "${album.keywordType.name.lowercase()} \u00B7 ${album.searchType.name.lowercase()} \u00B7 ${album.entries.size}"
    }

    data object Loading : ExtractorAlbumViewerScreenState
}

fun ExtractorAlbumViewerScreenState.album() = when (this) {
    is ExtractorAlbumViewerScreenState.Content -> this.album
    ExtractorAlbumViewerScreenState.Loading -> panic("Accessing album outside of content state.")
}

fun ExtractorAlbumViewerScreenState.albumEntries() = when (this) {
    is ExtractorAlbumViewerScreenState.Content -> album.entries
    else -> panic("Accessing album outside of content state.")
}

sealed interface ExtractorAlbumDialogSelection {

    data object None : ExtractorAlbumDialogSelection

    data object ConfirmShare : ExtractorAlbumDialogSelection

    data object ConfirmDelete : ExtractorAlbumDialogSelection
}