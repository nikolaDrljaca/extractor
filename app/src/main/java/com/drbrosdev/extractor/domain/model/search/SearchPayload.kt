package com.drbrosdev.extractor.domain.model.search

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType

/*
1. query data with dateRange - Full
2. query data with no dateRange - OnlyQuery
3. blank query with dateRange - OnlyDate
 */
sealed interface SearchPayload {
    data class Full(
        val query: String,
        val keywordType: KeywordType,
        val searchType: SearchType,
        val dateRange: DateRange,
    ) : SearchPayload

    data class OnlyQuery(
        val query: String,
        val keywordType: KeywordType,
        val searchType: SearchType,
    ) : SearchPayload

    data class OnlyDate(
        val dateRange: DateRange,
    ) : SearchPayload
}

fun SearchPayload.isBlank() = when (this) {
    is SearchPayload.Full -> query.isBlank()
    is SearchPayload.OnlyQuery -> query.isBlank()
    // only date range payload is never blank
    is SearchPayload.OnlyDate -> false
}