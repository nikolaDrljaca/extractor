package com.drbrosdev.extractor.ui.albumviewer

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album

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

fun ExtractorAlbumViewerScreenState.getAlbum() = when (this) {
    is ExtractorAlbumViewerScreenState.Content -> this.album
    ExtractorAlbumViewerScreenState.Loading -> error("Accessing album outside of content state.")
}

sealed interface ExtractorAlbumDialogSelection {

    data object None : ExtractorAlbumDialogSelection

    data object ConfirmShare: ExtractorAlbumDialogSelection

    data object ConfirmDelete: ExtractorAlbumDialogSelection

    data object BottomSheet: ExtractorAlbumDialogSelection
}