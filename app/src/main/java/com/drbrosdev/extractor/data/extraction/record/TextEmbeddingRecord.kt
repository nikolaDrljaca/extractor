package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.model.Embed


@Entity(tableName = "text_embedding")
data class TextEmbeddingRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "extraction_id") val extractionId: Long,

    val value: String
)

fun TextEmbeddingRecord.toEmbed() = Embed.Text(value = this.value)