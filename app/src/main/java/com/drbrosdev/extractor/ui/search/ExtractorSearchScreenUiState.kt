package com.drbrosdev.extractor.ui.search

import com.drbrosdev.extractor.domain.model.MediaImage



sealed class ExtractorSearchScreenUiState {

    data class Success(
        val images: List<MediaImage>,
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

}
