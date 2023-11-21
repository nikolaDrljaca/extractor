package com.drbrosdev.extractor.ui.components.extractordatefilter

import java.time.LocalDateTime

sealed interface ExtractorDateFilterStrategy {
    data object None : ExtractorDateFilterStrategy

    data class ByDateRange(
        val start: LocalDateTime,
        val end: LocalDateTime
    ) : ExtractorDateFilterStrategy
}