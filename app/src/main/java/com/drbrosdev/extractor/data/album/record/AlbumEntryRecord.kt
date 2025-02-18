package com.drbrosdev.extractor.data.album.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri


@Entity(tableName = "album_entry")
data class AlbumEntryRecord(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uri: String,

    @ColumnInfo("album_id")
    val albumId: Long,

    @ColumnInfo(name = "image_id")
    val imageEntityId: Long,
)

fun AlbumEntryRecord.toAlbumEntry(): AlbumEntry {
    return AlbumEntry(
        uri = MediaImageUri(this.uri),
        id = MediaImageId(this.id)
    )
}
