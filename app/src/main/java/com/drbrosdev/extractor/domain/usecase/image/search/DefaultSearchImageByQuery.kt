package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.core.toOption
import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.record.toExtraction
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.contains
import com.drbrosdev.extractor.domain.usecase.CreateAdaptedQuery
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class DefaultSearchImageByQuery(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText,
    private val createAdaptedQuery: CreateAdaptedQuery
) : SearchImageByQuery {

    override suspend fun execute(params: SearchImageByQuery.Params): List<Extraction> =
        withContext(dispatcher) {
            val cleanQuery = tokenizeText.invoke(params.query)
                .toList()

            val createAdaptedQueryParams = CreateAdaptedQuery.Params(
                tokens = cleanQuery,
                searchType = params.type,
                keywordType = params.keywordType
            )

            val adaptedQuery = createAdaptedQuery.invoke(createAdaptedQueryParams)

            val imageEntitySequence = imageEmbedDao.findByKeyword(adaptedQuery.query)
                .map { it.imageEntity.toExtraction() }

            params.dateRange.toOption()
                .fold(
                    ifSome = { dateRange -> imageEntitySequence.filter { it.byDateRange(dateRange) } },
                    ifEmpty = { imageEntitySequence }
                )
        }

    private fun Extraction.byDateRange(range: DateRange?): Boolean {
        if (range == null) return true
        return dateAdded in range
    }
}