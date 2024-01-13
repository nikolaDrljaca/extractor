package com.drbrosdev.extractor.domain.model


data class SuggestedSearch(
    val query: String,
    val keywordType: KeywordType,
    val searchType: SearchType
)