package com.drbrosdev.extractor.ui.search

import com.drbrosdev.extractor.domain.model.MediaImage
import kotlinx.collections.immutable.ImmutableList


sealed class ExtractorSearchScreenUiState {

    data class Success(
        val images: ImmutableList<MediaImage>,
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

}
