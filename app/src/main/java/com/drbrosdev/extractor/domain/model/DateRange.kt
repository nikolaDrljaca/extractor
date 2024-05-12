package com.drbrosdev.extractor.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DateRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)

infix fun LocalDateTime.isIn(range: DateRange): Boolean {
    return this.isAfter(range.start) && this.isBefore(range.end)
}

fun DateRange.asAlbumName(): String {
    // Format: Feb '24 - Jul '24
    // '' -> the first ' escapes the second
    val formatter = DateTimeFormatter.ofPattern("LLL ''yy")

    return buildString {
        append(start.format(formatter))
        append(" - ")
        append(end.format(formatter))
    }
}
