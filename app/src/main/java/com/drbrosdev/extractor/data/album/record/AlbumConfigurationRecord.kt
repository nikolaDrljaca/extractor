package com.drbrosdev.extractor.data.album.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType


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

fun SearchType.toAlbumSearchType(): AlbumConfigurationRecord.SearchType = when (this) {
    SearchType.FULL -> AlbumConfigurationRecord.SearchType.FULL
    SearchType.PARTIAL -> AlbumConfigurationRecord.SearchType.PARTIAL
}

fun KeywordType.toAlbumLabelType(): AlbumConfigurationRecord.LabelType = when (this) {
    KeywordType.ALL -> AlbumConfigurationRecord.LabelType.ALL
    KeywordType.TEXT -> AlbumConfigurationRecord.LabelType.TEXT
    KeywordType.IMAGE -> AlbumConfigurationRecord.LabelType.IMAGE
}

fun AlbumConfigurationRecord.LabelType.toLabelType(): KeywordType = when (this) {
    AlbumConfigurationRecord.LabelType.ALL -> KeywordType.ALL
    AlbumConfigurationRecord.LabelType.TEXT -> KeywordType.TEXT
    AlbumConfigurationRecord.LabelType.IMAGE -> KeywordType.IMAGE
}

fun AlbumConfigurationRecord.SearchType.toSearchType(): SearchType = when (this) {
    AlbumConfigurationRecord.SearchType.FULL -> SearchType.FULL
    AlbumConfigurationRecord.SearchType.PARTIAL -> SearchType.PARTIAL
}
