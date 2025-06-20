package com.drbrosdev.extractor.data.extraction.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.extraction.record.DescriptionEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.LupaImageMetadataRecord
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.toMetadata
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageAnnotations


data class ImageEmbeddingsRelation(
    @Embedded val imageEntity: LupaImageMetadataRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "lupa_image_id"
    )
    val textEmbeddingRecord: TextEmbeddingRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "lupa_image_id"
    )
    val visualEmbeddingRecord: VisualEmbeddingRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "lupa_image_id"
    )
    val userEmbeddingRecord: UserEmbeddingRecord,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "lupa_image_id"
    )
    val descriptionEmbeddingRecord: DescriptionEmbeddingRecord
)

fun ImageEmbeddingsRelation.toLupaImage(): LupaImage {
    return LupaImage(
        metadata = imageEntity.toMetadata(),
        annotations = toLupaAnnotations()
    )
}

fun ImageEmbeddingsRelation.toLupaAnnotations(): LupaImageAnnotations {
    val textEmbed = this.textEmbeddingRecord.value
    val descriptionEmbed = this.descriptionEmbeddingRecord.value

    val visualEmbeds = when {
        this.visualEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.visualEmbeddingRecord.value
            .split(",")
            .map { it }
    }

    val userEmbeds = when {
        this.userEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.userEmbeddingRecord.value
            .split(",")
            .map { it }
    }
    return LupaImageAnnotations(
        textEmbed = textEmbed,
        visualEmbeds = visualEmbeds,
        userEmbeds = userEmbeds,
        descriptionEmbed = descriptionEmbed
    )
}
