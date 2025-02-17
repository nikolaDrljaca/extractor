package com.drbrosdev.extractor.data.extraction.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.extraction.record.ExtractionRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord

data class UserExtractionRelation(
    @Embedded val extractionRecord: ExtractionRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_id"
    )
    val userEmbedEntity: UserEmbeddingRecord
)
