package com.drbrosdev.extractor.data.album.record

import androidx.room.Entity
import androidx.room.PrimaryKey

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

