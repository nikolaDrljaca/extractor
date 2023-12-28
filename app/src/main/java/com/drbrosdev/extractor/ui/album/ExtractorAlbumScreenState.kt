package com.drbrosdev.extractor.ui.album

import com.drbrosdev.extractor.domain.model.Album

sealed interface ExtractorAlbumScreenState {
    data class Content(
        val album: Album
    ): ExtractorAlbumScreenState {
        val metadata =
            "${album.labelType.name.lowercase()} \u00B7 ${album.searchType.name.lowercase()} \u00B7 ${album.entries.size}"
    }

    data object Loading: ExtractorAlbumScreenState
}