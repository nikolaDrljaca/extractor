package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.usecase.CreateAdaptedQuery
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.util.toExtraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class DefaultSearchImageByKeyword(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText,
    private val createAdaptedQuery: CreateAdaptedQuery
) : SearchImageByKeyword {

    override suspend fun execute(params: SearchImageByKeyword.Params): List<Extraction> =
        withContext(dispatcher) {
            val cleanQuery = tokenizeText(params.query)
                .toList()

            val createAdaptedQueryParams = CreateAdaptedQuery.Params(
                tokens = cleanQuery,
                searchType = params.type,
                keywordType = params.keywordType
            )

            val adaptedQuery = createAdaptedQuery(createAdaptedQueryParams)

            imageEmbedDao.findByKeyword(adaptedQuery.query)
                .parMap(context = dispatcher) { it.imageEntity.toExtraction() }
                .filterByDateRange(params.dateRange)
        }

    private fun List<Extraction>.filterByDateRange(range: DateRange?) = when {
        range != null -> this.filter { it.dateAdded isIn range }
        else -> this
    }
}