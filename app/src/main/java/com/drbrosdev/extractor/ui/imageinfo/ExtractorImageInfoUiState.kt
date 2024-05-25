package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.MediaImageId


@Immutable
data class ExtractorImageInfoUiState(
    val mediaImageId: MediaImageId = MediaImageId(0L),
    val userEmbedding: List<UserEmbedUiModel> = emptyList(),
    val visualEmbedding: List<VisualEmbedUiModel> = emptyList(),
)

@Immutable
data class VisualEmbedUiModel(
    val text: String,
    val isChecked: Boolean,
)

@Immutable
data class UserEmbedUiModel(
    val text: String,
    val isChecked: Boolean
)
