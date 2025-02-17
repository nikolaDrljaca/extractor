package com.drbrosdev.extractor.data.extraction.record

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
