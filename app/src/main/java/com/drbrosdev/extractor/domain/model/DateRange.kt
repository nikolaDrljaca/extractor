package com.drbrosdev.extractor.domain.model

import java.time.LocalDateTime

data class DateRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)

infix fun LocalDateTime.isIn(range: DateRange) : Boolean {
    return this.isAfter(range.start) && this.isBefore(range.end)
}
