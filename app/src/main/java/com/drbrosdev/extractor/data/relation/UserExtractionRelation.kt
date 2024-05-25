package com.drbrosdev.extractor.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity

data class UserExtractionRelation(
    @Embedded val extractionEntity: ExtractionEntity,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_entity_id"
    )
    val userEmbedEntity: UserEmbeddingEntity
)
