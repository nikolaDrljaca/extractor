package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "album_entry")
data class AlbumEntryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uri: String,

    @ColumnInfo("album_entity_id")
    val albumId: Long,

    @ColumnInfo(name = "image_entity_id")
    val imageEntityId: Long,
)
