package com.drbrosdev.extractor.domain.model

data class ImageSearchParams(
    val query: String,
    val keywordType: KeywordType,
    val dateRange: DateRange?,
    val searchType: SearchType
)