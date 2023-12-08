package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.Extraction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


@Stable
sealed class ExtractorSearchScreenUiState {

    data class Success(
        val images: ImmutableList<Extraction>,
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

    data object FirstSearch: ExtractorSearchScreenUiState()

    data object Empty: ExtractorSearchScreenUiState()
}

fun MutableStateFlow<ExtractorSearchScreenUiState>.createFrom(
    mediaImages: List<Extraction>,
) = update {
    when {
        mediaImages.isEmpty() -> ExtractorSearchScreenUiState.Empty
        else -> ExtractorSearchScreenUiState.Success(mediaImages.toImmutableList())
    }
}
