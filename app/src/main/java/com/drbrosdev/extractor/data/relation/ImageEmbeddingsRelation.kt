package com.drbrosdev.extractor.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity


data class ImageEmbeddingsRelation(
    @Embedded val imageEntity: ExtractionEntity,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_entity_id"
    )
    val textEmbeddingEntity: TextEmbeddingEntity,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_entity_id"
    )
    val visualEmbeddingEntities: List<VisualEmbeddingEntity>,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_entity_id"
    )
    val userEmbeddingEntity: UserEmbeddingEntity?,
)