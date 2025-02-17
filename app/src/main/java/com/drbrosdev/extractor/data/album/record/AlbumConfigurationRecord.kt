package com.drbrosdev.extractor.data.album.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "album_configuration")
data class AlbumConfigurationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "album_id")
    val albumId: Long,

    val keyword: String,

    @ColumnInfo(name = "search_type")
    val searchType: SearchType,

    @ColumnInfo(name = "label_type")
    val labelType: LabelType
) {
    enum class SearchType {
        FULL,
        PARTIAL
    }

    enum class LabelType {
        ALL,
        TEXT,
        IMAGE
    }
}
