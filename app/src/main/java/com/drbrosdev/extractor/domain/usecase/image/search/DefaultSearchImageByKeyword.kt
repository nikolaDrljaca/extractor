package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.util.logInfo
import com.drbrosdev.extractor.util.toExtraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class DefaultSearchImageByKeyword(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText
) : SearchImageByKeyword {

    override suspend fun execute(params: SearchImageByKeyword.Params): List<Extraction> =
        withContext(dispatcher) {
            val cleanQuery = tokenizeText(params.query)
                .map { it.text }
                .toList()

            logInfo("Clean query: $cleanQuery")

            val visualAdaptedQuery = buildString {
                append("%")
                append(
                    cleanQuery
                        .sorted()
                        .joinToString(separator = "%") { it }
                )
                append("%")
            }

            val ftsAdaptedQuery = buildString {
                append(cleanQuery.joinToString(separator = " ") { it })
                append("*")
            }

            logInfo("Visual Query: $visualAdaptedQuery | FTS query: $ftsAdaptedQuery")

            val result = when (params.keywordType) {
                KeywordType.ALL -> imageEmbedDao.findByKeyword(visualAdaptedQuery, ftsAdaptedQuery)
                KeywordType.TEXT -> imageEmbedDao.findByTextEmbeddingFts(ftsAdaptedQuery)
                KeywordType.IMAGE -> imageEmbedDao.findByVisualEmbedding(visualAdaptedQuery)
            }

            logInfo("Result set size: ${result.size}")

            result
                .parMap(context = dispatcher) { it.imageEntity.toExtraction() }
                .filterByDateRange(params.dateRange)
        }

    private fun List<Extraction>.filterByDateRange(range: DateRange?) = when {
        range != null -> this.filter { it.dateAdded isIn range }
        else -> this
    }
}