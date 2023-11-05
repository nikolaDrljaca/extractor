package com.drbrosdev.extractor.ui.imageinfo

import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import com.drbrosdev.extractor.ui.components.embeddingsform.EmbeddingsFormState


data class ImageInfoUiModel(
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

fun ImageDataWithEmbeddings.mapToInfoModel(): ImageInfoUiModel {
    return ImageInfoUiModel(
        mediaImageId = this.imageEntity.mediaStoreId,
        userEmbedding = this.userEmbedding?.value,
        textEmbedding = this.textEmbedding.value,
        visualEmbedding = this.visualEmbeddings.map { it.value.mapToVisualEmbedUiModel() },
    )
}
