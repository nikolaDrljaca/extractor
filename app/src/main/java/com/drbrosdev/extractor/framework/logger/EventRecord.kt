package com.drbrosdev.extractor.framework.logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "event")
data class EventRecord(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "event_order") val eventOrder: Long = 0, // Used to track log order, timestamp is string format

    val tag: String,

    val message: String,

    val timestamp: LocalDateTime,

    @ColumnInfo(name = "localized_message")
    val localizedMessage: String = ""
)
