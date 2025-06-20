package com.drbrosdev.extractor.data.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_index")
data class SearchIndexRecord(
    // indices
    @ColumnInfo(name = "text_index")
    val textIndex: String,

    @ColumnInfo(name = "visual_index")
    val visualIndex: String,

    @ColumnInfo(name = "user_index")
    val userIndex: String,

    @ColumnInfo(name = "description_index")
    val descriptionIndex: String,

    // relation
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "extraction_id") val extractionId: Long
)