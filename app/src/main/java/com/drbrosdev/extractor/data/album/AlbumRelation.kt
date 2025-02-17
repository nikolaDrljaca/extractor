package com.drbrosdev.extractor.data.album

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import com.drbrosdev.extractor.data.album.record.AlbumRecord


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
