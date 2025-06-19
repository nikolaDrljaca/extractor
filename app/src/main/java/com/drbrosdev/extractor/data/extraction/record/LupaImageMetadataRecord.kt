package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import java.time.LocalDateTime

@Entity(
    tableName = "lupa_image"
)
data class LupaImageMetadataRecord(
    @ColumnInfo(name = "media_store_id")
    @PrimaryKey
    val mediaStoreId: Long,

    val uri: String,

    @ColumnInfo(name = "date_added")
    val dateAdded: LocalDateTime,

    val path: String
)

fun LupaImageMetadataRecord.toMetadata() = LupaImageMetadata(
    mediaImageId = MediaImageId(this.mediaStoreId),
    uri = MediaImageUri(this.uri),
    path = this.path,
    dateAdded = this.dateAdded
)
