package com.drbrosdev.extractor.ui.album

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album

sealed interface ExtractorAlbumScreenState {

    @Immutable
    data class Content(
        val album: Album,
        val isConfirmDeleteShown: Boolean = false
    ) : ExtractorAlbumScreenState {
        val metadata =
            "${album.labelType.name.lowercase()} \u00B7 ${album.searchType.name.lowercase()} \u00B7 ${album.entries.size}"
    }

    data object Loading : ExtractorAlbumScreenState
}