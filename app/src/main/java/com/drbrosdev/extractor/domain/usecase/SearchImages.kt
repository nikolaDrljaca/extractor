package com.drbrosdev.extractor.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
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
            if (dataStore.getSearchCount() == 0) {
                return@withContext Unit.left()
            }

            val out = when {
                shouldUseDateRangeSearch(params) -> {
                    // NOTE: Should never be empty based on the above check
                    params.dateRange.toOption()
                        .fold(
                            ifEmpty = { emptyList() },
                            ifSome = { searchImageByDateRange.execute(it) }
                        )
                }

                else -> {
                    searchImageByQuery.execute(params)
                }
            }


            dataStore.decrementSearchCount()
            out.right()
        }


    private fun shouldUseDateRangeSearch(params: SearchImageByQuery.Params): Boolean {
        return (params.query.isBlank()) and (params.dateRange != null)
    }
}