package com.drbrosdev.extractor.ui.imageinfo

import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import com.drbrosdev.extractor.ui.components.embeddingsform.EmbeddingsFormState


data class ExtractorImageInfoUiState(
    val mediaImageId: Long = 0L,
    val userEmbedding: String? = null,
    val textEmbedding: String = "",
    val visualEmbedding: List<VisualEmbedUiModel> = emptyList(),
) {
    val embeddingsFormState = EmbeddingsFormState.create(textEmbedding, userEmbedding ?: "")
}

data class VisualEmbedUiModel(
    val text: String,
    val isChecked: Boolean
)

fun String.mapToVisualEmbedUiModel(): VisualEmbedUiModel {
    return VisualEmbedUiModel(this, false)
}

fun ImageDataWithEmbeddings.mapToInfoModel(): ExtractorImageInfoUiState {
    return ExtractorImageInfoUiState(
        mediaImageId = this.imageEntity.mediaStoreId,
        userEmbedding = this.userEmbeddingEntity?.value,
        textEmbedding = this.textEmbeddingEntity.value,
        visualEmbedding = this.visualEmbeddingEntities.map { it.value.mapToVisualEmbedUiModel() },
    )
}
