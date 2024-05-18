package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.ui.components.embeddingsform.EmbeddingsFormState


@Immutable
data class ExtractorImageInfoUiState(
    val mediaImageId: MediaImageId = MediaImageId(0L),
    val textEmbedding: String = "",
    val userEmbedding: String? = null,
    val visualEmbedding: List<VisualEmbedUiModel> = emptyList(),
) {
    val embeddingsFormState = EmbeddingsFormState(textEmbedding, userEmbedding ?: "")
}

@Immutable
data class VisualEmbedUiModel(
    val text: String,
    val isChecked: Boolean,
)

fun Embed.Visual.mapToUiModel(): VisualEmbedUiModel {
    return VisualEmbedUiModel(
        text = this.value,
        isChecked = false,
    )
}