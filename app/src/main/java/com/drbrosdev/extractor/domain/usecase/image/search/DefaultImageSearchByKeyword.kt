package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.repository.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.util.logInfo
import com.drbrosdev.extractor.util.toExtraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultImageSearchByKeyword(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
) : ImageSearchByKeyword {

    override suspend fun search(params: ImageSearchByKeyword.Params): List<Extraction> =
        withContext(dispatcher) {
            val result = with(params) {
                query
                    .prepareQuery()
                    .mapToSearchStrategy(type)
                    .findBy(keywordType)
                    .filterByDateRange(dateRange)
            }

            result
        }

    private fun String.prepareQuery(): String {
        return this
            .trim()
            .replace(Regex("\\s+"), " ")
            .lowercase()
    }

    private fun String.mapToSearchStrategy(type: SearchType): ImageEmbeddingSearchStrategy {
        return when (type) {
            SearchType.FULL -> ImageEmbeddingSearchStrategy.Full(this)
            SearchType.PARTIAL -> ImageEmbeddingSearchStrategy.Partial(this)
        }
    }

    private suspend fun ImageEmbeddingSearchStrategy.findBy(keywordType: KeywordType): List<Extraction> {
        logInfo("Search query/keyword: $query")
        val imageEmbeddingRelations = when (keywordType) {
            KeywordType.ALL -> imageEmbedDao.findByKeyword(query)
            KeywordType.TEXT -> imageEmbedDao.findByTextEmbeddingFts(query)
            KeywordType.IMAGE -> imageEmbedDao.findByVisualEmbedding(query)
        }

        return imageEmbeddingRelations
            .parMap(context = dispatcher) { it.imageEntity.toExtraction() }
    }

    private fun List<Extraction>.filterByDateRange(range: DateRange?) = when {
        range != null -> this.filter { it.dateAdded isIn range }
        else -> this
    }
}