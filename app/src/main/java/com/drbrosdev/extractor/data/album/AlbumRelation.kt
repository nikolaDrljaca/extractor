package com.drbrosdev.extractor.data.album

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import com.drbrosdev.extractor.data.album.record.toAlbumEntry
import com.drbrosdev.extractor.data.album.record.toLabelType
import com.drbrosdev.extractor.data.album.record.toSearchType
import com.drbrosdev.extractor.domain.model.Album


data class AlbumRelation(
    @Embedded
    val albumRecord: AlbumRecord,

    @Relation(
        parentColumn = "id",
        entityColumn = "album_id"
    )
    val configuration: AlbumConfigurationRecord,

    @Relation(
        parentColumn = "id",
        entityColumn = "album_id"
    )
    val entries: List<AlbumEntryRecord>
)

fun AlbumRelation.toAlbum(): Album {
    return Album(
        id = this.albumRecord.id,
        name = this.albumRecord.name,
        keyword = this.configuration.keyword,
        searchType = this.configuration.searchType.toSearchType(),
        keywordType = this.configuration.labelType.toLabelType(),
        entries = this.entries.map { it.toAlbumEntry() }
    )
}

