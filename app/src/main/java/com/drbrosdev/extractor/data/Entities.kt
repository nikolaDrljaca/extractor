package com.drbrosdev.extractor.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_data_entity",
)
data class ImageDataEntity(
    @ColumnInfo(name = "media_store_id")
    @PrimaryKey
    val mediaStoreId: Long,
    val uri: String,
    val labels: String,
)

@Entity(tableName = "previous_search_entity")
data class PreviousSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val resultCount: Int
)