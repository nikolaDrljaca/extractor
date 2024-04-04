package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PerformSearch(
    private val dispatcher: CoroutineDispatcher,
    private val searchImageByKeyword: SearchImageByKeyword,
    private val dataStore: ExtractorDataStore
) {

    suspend fun execute(params: SearchImageByKeyword.Params): Result<List<Extraction>> =
        withContext(dispatcher) {
            val currentSearchCount = dataStore.getSearchCount()
            if (currentSearchCount == 0) {
                return@withContext Result.failure(IllegalStateException("Not enough searches left."))
            }

            val out = searchImageByKeyword.execute(params)
            dataStore.decrementSearchCount()
            Result.success(out)
        }
}