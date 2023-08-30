package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.PreviousSearchDao
import com.drbrosdev.extractor.data.PreviousSearchEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InsertPreviousSearch(
    private val dispatcher: CoroutineDispatcher,
    private val dao: PreviousSearchDao
) {

    suspend operator fun invoke(query: String, resultCount: Int) = withContext(dispatcher) {
        val out = when (val existing = dao.findByQuery(query)) {
            null -> PreviousSearchEntity(query, resultCount)
            else -> existing.copy(resultCount = resultCount)
        }
        dao.insert(out)
    }
}