package com.drbrosdev.extractor.ui.search

import android.os.Parcelable
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
sealed interface SearchNavTargetArgs: Parcelable {

    data object Empty: SearchNavTargetArgs

    data class Args(
        val query: String,
        val keywordType: KeywordType,
        val startRange: String?,
        val endRange: String?,
        val searchType: SearchType
    ): SearchNavTargetArgs
}

fun SearchNavTargetArgs.toSearchParams(): ImageSearchParams {
    return when (this) {
        SearchNavTargetArgs.Empty -> {
            logEvent("Attempting to access searchNavTarget args when EMPTY was sent.")
            error("Attempting to access searchNavTarget args when none were sent.")
        }

        is SearchNavTargetArgs.Args -> {
            val dateRange = when {
                startRange != null && endRange != null -> DateRange(
                    start = LocalDateTime.parse(startRange),
                    end = LocalDateTime.parse(endRange)
                )

                else -> null
            }
            ImageSearchParams(
                query = query,
                keywordType = keywordType,
                searchType = searchType,
                dateRange = dateRange
            )
        }
    }
}