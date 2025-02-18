package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import java.time.LocalDateTime


@Entity(
    tableName = "extraction"
)
data class ExtractionRecord(
    @ColumnInfo(name = "media_store_id")
    @PrimaryKey
    val mediaStoreId: Long,

    val uri: String,

    @ColumnInfo(name = "date_added") val dateAdded: LocalDateTime,
    val path: String
)

fun ExtractionRecord.toExtraction() = Extraction(
    mediaImageId = MediaImageId(this.mediaStoreId),
    uri = MediaImageUri(this.uri),
    path = this.path,
    dateAdded = this.dateAdded
)
