package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import com.drbrosdev.extractor.domain.model.LabelType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RememberSearch(
    private val dispatcher: CoroutineDispatcher,
    private val dao: PreviousSearchDao
) {

    suspend operator fun invoke(params: Params) = withContext(dispatcher) {
        //Keep only the most recent 10 searches
        val current = dao.findAllAndTake(10)

        if (current.isNotEmpty()) dao.delete(current.last())

        val out = with(params) {
            when (val existing = dao.findByQueryAndLabelType(query, labelType)) {
                null -> PreviousSearchEntity(query, resultCount, labelType)
                else -> existing.copy(resultCount = resultCount, labelType = labelType)
            }
        }

        dao.insert(out)
    }

    data class Params(
        val query: String,
        val resultCount: Int,
        val labelType: LabelType
    )
}