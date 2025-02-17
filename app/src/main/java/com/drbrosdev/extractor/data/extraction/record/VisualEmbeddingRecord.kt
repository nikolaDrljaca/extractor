package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visual_embedding")
data class VisualEmbeddingRecord(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "extraction_id") val extractionId: Long,

    val value: String
)
