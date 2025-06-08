package com.drbrosdev.extractor.ui.components.albumoverview

import androidx.compose.runtime.Immutable

sealed interface ExtractorAlbumsUiModel {

    data object Empty: ExtractorAlbumsUiModel

    @Immutable
    data class Content(
        val albums: List<ExtractorAlbumOverview>,
        val onAlbumClick: (Long) -> Unit
    ) : ExtractorAlbumsUiModel
}
