package com.drbrosdev.extractor.domain.usecase.search

import com.drbrosdev.extractor.data.ExtractorDataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


/**
 * Emits true when [ExtractorDataStore.searchCount] changes from 0 to a positive value.
 */
class SearchCountPositiveDelta(
    private val dataStore: ExtractorDataStore
) {
    operator fun invoke() = flow {
        var currentCount = dataStore.getSearchCount()

        val positiveChanges = dataStore.searchCount
            .map { updated ->
                val out = (updated > currentCount)
                when {
                    out -> currentCount = updated
                    updated == 0 -> currentCount = 1
                }
                out
            }
        emitAll(positiveChanges)
    }
        .distinctUntilChanged()
}
