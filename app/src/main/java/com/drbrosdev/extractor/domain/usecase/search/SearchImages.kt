package com.drbrosdev.extractor.domain.usecase.search

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.search.toPayload
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SearchImages(
    private val dispatcher: CoroutineDispatcher,
    private val searchImageByQuery: SearchImageByQuery,
    private val sideEffects: SearchImageSideEffects,
    private val dataStore: ExtractorDataStore
) {
    suspend fun execute(imageSearchParams: ImageSearchParams): Either<Unit, List<LupaImage>> =
        withContext(dispatcher) {
            when {
                // check search preconditions
                dataStore.getSearchCount() == 0 -> Unit.left()

                else -> searchImageByQuery.execute(imageSearchParams.toPayload())
                    .right()
                    .also { sideEffects.execute() }
            }
        }
}