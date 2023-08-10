package com.drbrosdev.extractor.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_data_entity")
data class ImageDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "media_store_id") val mediaStoreId: Long,
    val uri: String,
    val labels: String,
)