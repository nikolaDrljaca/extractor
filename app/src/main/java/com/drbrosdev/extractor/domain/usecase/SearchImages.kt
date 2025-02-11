package com.drbrosdev.extractor.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SearchImages(
    private val dispatcher: CoroutineDispatcher,
    private val searchImageByQuery: SearchImageByQuery,
    private val searchImageByDateRange: SearchImageByDateRange,
    private val dataStore: ExtractorDataStore
) {
    suspend fun execute(params: SearchImageByQuery.Params): Either<Unit, List<Extraction>> =
        withContext(dispatcher) {
            when {
                dataStore.getSearchCount() == 0 -> Unit.left()

                else -> executeSearch(params).right().also {
                    dataStore.decrementSearchCount()
                }
            }
        }

    private suspend fun executeSearch(params: SearchImageByQuery.Params): List<Extraction> = when {
        shouldUseDateRangeSearch(params) -> {
            // NOTE: Should never be empty based on the above check
            val dateRange = requireNotNull(params.dateRange) {
                logEvent("SearchImages: shouldUseDateRangeSearch is true but dateRange is null!")
            }
            searchImageByDateRange.execute(dateRange)
        }

        else -> searchImageByQuery.execute(params)
    }

    private fun shouldUseDateRangeSearch(params: SearchImageByQuery.Params): Boolean {
        return (params.query.isBlank()) and (params.dateRange != null)
    }
}