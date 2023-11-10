package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import com.drbrosdev.extractor.domain.model.LabelType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InsertPreviousSearch(
    private val dispatcher: CoroutineDispatcher,
    private val dao: PreviousSearchDao
) {

    suspend operator fun invoke(
        query: String,
        resultCount: Int,
        labelType: LabelType
    ) = withContext(dispatcher) {
        val out = when (val existing = dao.findByQueryAndLabelType(query, labelType)) {
            null -> PreviousSearchEntity(query, resultCount, labelType)
            else -> existing.copy(resultCount = resultCount, labelType = labelType)
        }
        dao.insert(out)
    }
}