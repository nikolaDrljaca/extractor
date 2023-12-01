package com.drbrosdev.extractor.ui.search

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.MediaImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


@Stable
sealed class ExtractorSearchScreenUiState {

    data class Success(
        val images: ImmutableList<MediaImage>,
        val thumbnails: List<Bitmap>
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

    data object FirstSearch: ExtractorSearchScreenUiState()

    data object Empty: ExtractorSearchScreenUiState()
}

fun MutableStateFlow<ExtractorSearchScreenUiState>.createFrom(
    mediaImages: List<MediaImage>,
    thumbnails: List<Bitmap>
) = update {
    when {
        mediaImages.isEmpty() -> ExtractorSearchScreenUiState.Empty
        else -> ExtractorSearchScreenUiState.Success(mediaImages.toImmutableList(), thumbnails)
    }
}
