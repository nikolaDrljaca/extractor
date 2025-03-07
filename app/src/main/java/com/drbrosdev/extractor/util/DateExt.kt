package com.drbrosdev.extractor.util

import com.drbrosdev.extractor.domain.model.DateRange
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDateTime.asAlbumName() = buildString {
    // Format: Feb '24 - Jul '24
    // '' -> the first ' escapes the second
    val formatter = DateTimeFormatter.ofPattern("LLL ''yy")
    append(this@asAlbumName.format(formatter))
}

@JvmInline
value class EpochMillis(val value: Long)

fun EpochMillis.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault())

fun LocalDateTime?.asDisplayString(default: String): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return when {
        this == null -> default
        else -> this.format(formatter)
    }
}

infix fun LocalDateTime.isIn(range: DateRange): Boolean {
    return this.isAfter(range.start) && this.isBefore(range.end)
}
