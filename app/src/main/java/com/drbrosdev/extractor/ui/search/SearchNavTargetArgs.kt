package com.drbrosdev.extractor.ui.search

import android.os.Parcelable
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class SearchNavTargetArgs(
    val query: String,
    val keywordType: KeywordType,
    val startRange: String?,
    val endRange: String?,
    val searchType: SearchType
) : Parcelable

fun SearchNavTargetArgs.toSearchParams(): ImageSearchParams {
    val dateRange = when {
        startRange != null && endRange != null -> DateRange(
            start = LocalDateTime.parse(startRange),
            end = LocalDateTime.parse(endRange)
        )

        else -> null
    }
    return ImageSearchParams(
        query = query,
        keywordType = keywordType,
        searchType = searchType,
        dateRange = dateRange
    )
}

fun ImageSearchParams.asSearchNavTargetArgs() = SearchNavTargetArgs(
    query = query,
    keywordType = keywordType,
    searchType = searchType,
    startRange = dateRange?.start?.toString(),
    endRange = dateRange?.end?.toString()
)
