package com.drbrosdev.extractor.ui.album

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album

sealed interface ExtractorAlbumScreenState {

    @Immutable
    data class Content(
        val album: Album,
        val dialogSelection: ExtractorAlbumDialogSelection,
        val shouldShowSelectBar: Boolean = false
    ) : ExtractorAlbumScreenState {
        val metadata =
            "${album.keywordType.name.lowercase()} \u00B7 ${album.searchType.name.lowercase()} \u00B7 ${album.entries.size}"
    }

    data object Loading : ExtractorAlbumScreenState
}

fun ExtractorAlbumScreenState.getAlbum() = when (this) {
    is ExtractorAlbumScreenState.Content -> this.album
    ExtractorAlbumScreenState.Loading -> error("Accessing album outside of content state.")
}

sealed interface ExtractorAlbumDialogSelection {

    data object None : ExtractorAlbumDialogSelection

    data object ConfirmShare: ExtractorAlbumDialogSelection

    data object ConfirmDelete: ExtractorAlbumDialogSelection

    data object BottomSheet: ExtractorAlbumDialogSelection
}