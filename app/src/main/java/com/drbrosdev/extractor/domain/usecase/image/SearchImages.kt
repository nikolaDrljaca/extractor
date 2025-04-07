package com.drbrosdev.extractor.domain.usecase.image

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.isBlank
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SearchImages(
    private val dispatcher: CoroutineDispatcher,
    private val searchImageByQuery: SearchImageByQuery,
    private val searchImageByDateRange: SearchImageByDateRange,
    private val sideEffects: SearchImageSideEffects,
    private val dataStore: ExtractorDataStore
) {
    suspend fun execute(imageSearchParams: ImageSearchParams): Either<Unit, List<Extraction>> =
        withContext(dispatcher) {
            when {
                dataStore.getSearchCount() == 0 -> Unit.left()

                else -> executeSearch(imageSearchParams).right().also {
                    sideEffects.execute()
                }
            }
        }

    private suspend fun executeSearch(imageSearchParams: ImageSearchParams): List<Extraction> =
        when {
            // no search is done for blank query and blank dateRange
            imageSearchParams.isBlank() -> emptyList()

            shouldUseDateRangeSearch(imageSearchParams) -> {
                // NOTE: Should never be empty based on the above check
                val dateRange = requireNotNull(imageSearchParams.dateRange) {
                    logEvent("SearchImages: shouldUseDateRangeSearch is true but dateRange is null!")
                }
                searchImageByDateRange.execute(dateRange)
            }

            else -> searchImageByQuery.execute(imageSearchParams)
        }

    private fun shouldUseDateRangeSearch(imageSearchParams: ImageSearchParams): Boolean {
        return (imageSearchParams.query.isBlank()) and (imageSearchParams.dateRange != null)
    }
}