package com.drbrosdev.extractor.domain.model.search

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType

data class ImageSearchParams(
    val query: String,
    val keywordType: KeywordType,
    val dateRange: DateRange?,
    val searchType: SearchType
)

fun ImageSearchParams.isBlank(): Boolean {
    return query.isBlank() && dateRange == null
}

fun ImageSearchParams.isNotBlank(): Boolean = isBlank().not()

fun ImageSearchParams.toPayload(): SearchPayload {
    return when (dateRange) {
        // only query scenario
        null -> SearchPayload.OnlyQuery(
            query = query,
            keywordType = keywordType,
            searchType = searchType
        )

        else -> {
            // date is not null
            if (query.isBlank()) {
                return SearchPayload.OnlyDate(dateRange)
            }
            return SearchPayload.Full(
                query = query,
                keywordType = keywordType,
                searchType = searchType,
                dateRange = dateRange
            )
        }
    }
}
