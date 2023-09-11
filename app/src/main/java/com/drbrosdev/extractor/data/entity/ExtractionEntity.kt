package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "previous_search_entity")
data class PreviousSearchEntity(
    @PrimaryKey val query: String,
    val resultCount: Int
)

@Entity(
    tableName = "image_extraction_entity"
)
data class ExtractionEntity(
    @ColumnInfo(name = "media_store_id")
    @PrimaryKey
    val mediaStoreId: Long,
    val uri: String
)
