package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visual_embedding")
data class VisualEmbeddingRecord(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "lupa_image_id") val lupaImageId: Long,

    val value: String
) {
    companion object {
        const val SEPARATOR = ","
    }
}