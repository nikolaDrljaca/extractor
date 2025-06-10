package com.drbrosdev.extractor.ui.components.albumoverview

import androidx.compose.runtime.Immutable

sealed interface ExtractorAlbumsUiState {

    data object Empty: ExtractorAlbumsUiState

    @Immutable
    data class Content(
        val albums: List<ExtractorAlbumOverview>,
        val onAlbumClick: (Long) -> Unit
    ) : ExtractorAlbumsUiState
}
