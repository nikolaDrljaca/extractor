package com.drbrosdev.extractor.domain.model.search

import com.drbrosdev.extractor.domain.model.KeywordType


data class SuggestedSearch(
    val query: String,
    val keywordType: KeywordType,
    val searchType: SearchType
)