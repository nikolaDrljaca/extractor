package com.drbrosdev.extractor.data.extraction.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.extraction.record.ExtractionRecord
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds


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

fun ImageEmbeddingsRelation.toImageEmbeds(): ImageEmbeds {
    val textEmbed = Embed.Text(value = this.textEmbeddingRecord.value)

    val visualEmbeds = when {
        this.visualEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.visualEmbeddingRecord.value
            .split(",")
            .map { Embed.Visual(it) }
    }

    val userEmbeds = when {
        this.userEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.userEmbeddingRecord.value
            .split(",")
            .map { Embed.User(it) }
    }

    return ImageEmbeds(
        textEmbed = textEmbed,
        visualEmbeds = visualEmbeds,
        userEmbeds = userEmbeds
    )
}
