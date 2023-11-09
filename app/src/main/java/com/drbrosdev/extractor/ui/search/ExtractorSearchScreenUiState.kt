package com.drbrosdev.extractor.ui.search

import com.drbrosdev.extractor.domain.model.MediaImageInfo



sealed class ExtractorSearchScreenUiState {

    data class Success(
        val images: List<MediaImageInfo>,
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

}
