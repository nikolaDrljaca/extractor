package com.drbrosdev.extractor.data.extraction.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.extraction.record.ExtractionRecord
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord


data class ImageEmbeddingsRelation(
    @Embedded val imageEntity: ExtractionRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_id"
    )
    val textEmbeddingRecord: TextEmbeddingRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_id"
    )
    val visualEmbeddingRecord: VisualEmbeddingRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "extraction_id"
    )
    val userEmbeddingRecord: UserEmbeddingRecord,
)