package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.core.toOption
import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.record.toExtraction
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.contains
import com.drbrosdev.extractor.domain.usecase.image.BuildFtsQuery
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class DefaultSearchImageByQuery(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText,
    private val buildFtsQuery: BuildFtsQuery
) : SearchImageByQuery {

    override suspend fun execute(imageSearchParams: ImageSearchParams): List<Extraction> =
        withContext(dispatcher) {
            val cleanQuery = tokenizeText.invoke(imageSearchParams.query)
                .toList()

            val buildFtsQueryParams = BuildFtsQuery.Params(
                tokens = cleanQuery,
                searchType = imageSearchParams.searchType,
                keywordType = imageSearchParams.keywordType
            )

            val adaptedQuery = buildFtsQuery.invoke(buildFtsQueryParams)

            val imageEntitySequence = imageEmbedDao.findByKeyword(adaptedQuery.value)
                .map { it.imageEntity.toExtraction() }

            imageSearchParams.dateRange.toOption()
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