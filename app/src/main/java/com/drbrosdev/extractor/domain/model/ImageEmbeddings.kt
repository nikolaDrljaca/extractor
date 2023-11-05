package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity

data class ImageEmbeddings(
    val textEmbeddingEntity: TextEmbeddingEntity,
    val visualEmbeddingEntities: List<VisualEmbeddingEntity>,
    val userEmbeddingEntity: UserEmbeddingEntity?
)

fun ImageDataWithEmbeddings.mapToImageEmbeddings(): ImageEmbeddings {
    return ImageEmbeddings(
        textEmbeddingEntity = this.textEmbeddingEntity,
        visualEmbeddingEntities = this.visualEmbeddingEntities,
        userEmbeddingEntity = this.userEmbeddingEntity
    )
}
