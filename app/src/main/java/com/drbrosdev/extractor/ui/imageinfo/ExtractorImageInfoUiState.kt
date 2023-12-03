package com.drbrosdev.extractor.ui.imageinfo

import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation
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
    val isChecked: Boolean,
    val id: Long
)

fun VisualEmbeddingEntity.mapToVisualEmbedUiModel(): VisualEmbedUiModel {
    return VisualEmbedUiModel(
        text = this.value,
        isChecked = false,
        id = this.id
    )
}

fun ImageEmbeddingsRelation.mapToInfoModel(): ExtractorImageInfoUiState {
    return ExtractorImageInfoUiState(
        mediaImageId = this.imageEntity.mediaStoreId,
        userEmbedding = this.userEmbeddingEntity?.value,
        textEmbedding = this.textEmbeddingEntity.value,
        visualEmbedding = this.visualEmbeddingEntities.map {
            it.mapToVisualEmbedUiModel()
        },
    )
}
