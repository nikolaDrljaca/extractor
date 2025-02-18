package com.drbrosdev.extractor.data.album.record

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum

@Entity(tableName = "album")
data class AlbumRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val origin: Origin
) {

    enum class Origin {
        VISUAL_COMPUTED,
        TEXT_COMPUTED,
        USER_GENERATED
    }
}

fun NewAlbum.Origin.toAlbumOrigin(): AlbumRecord.Origin = when (this) {
    NewAlbum.Origin.VISUAL_COMPUTED -> AlbumRecord.Origin.VISUAL_COMPUTED
    NewAlbum.Origin.TEXT_COMPUTED -> AlbumRecord.Origin.TEXT_COMPUTED
    NewAlbum.Origin.USER_GENERATED -> AlbumRecord.Origin.USER_GENERATED
}
