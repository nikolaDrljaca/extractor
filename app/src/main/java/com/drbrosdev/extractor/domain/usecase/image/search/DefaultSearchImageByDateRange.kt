package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.util.toExtraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultSearchImageByDateRange(
    private val dispatcher: CoroutineDispatcher,
    private val extractionDao: ExtractionDao
) : SearchImageByDateRange {

    override suspend fun execute(dateRange: DateRange): List<Extraction> {
        val start = dateRange.start.toString()
        val end = dateRange.end.toString()

        val out = extractionDao.findByDateRange(start, end)

        return withContext(dispatcher) {
            out
                .map { it.toExtraction() }
        }
    }
}