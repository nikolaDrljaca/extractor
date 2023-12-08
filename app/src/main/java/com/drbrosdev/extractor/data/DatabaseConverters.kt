package com.drbrosdev.extractor.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

object DatabaseConverters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return LocalDateTime.parse(value)
    }

}