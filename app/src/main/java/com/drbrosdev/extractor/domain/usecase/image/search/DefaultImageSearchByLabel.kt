package com.drbrosdev.extractor.domain.usecase.image.search

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.repository.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.domain.usecase.RememberSearch
import com.drbrosdev.extractor.util.runCatching
import com.drbrosdev.extractor.util.toExtraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultImageSearchByLabel(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val rememberSearch: RememberSearch
) : ImageSearchByLabel {

    override suspend fun search(params: ImageSearchByLabel.Params): List<Extraction> =
        withContext(dispatcher) {
            val result = with(params) {
                query
                    .prepareQuery()
                    .mapToSearchStrategy(type)
                    .findBy(labelType)
                    .filterByDateRange(dateRange)
            }

            if (params.query.isNotBlank()) runCatching {
                val rememberSearchParams = RememberSearch.Params(
                    query = params.query,
                    resultCount = result.size,
                    labelType = params.labelType
                )
                rememberSearch(rememberSearchParams)
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

    private suspend fun ImageEmbeddingSearchStrategy.findBy(labelType: LabelType): List<Extraction> {
        val imageEmbeddingRelations = when (labelType) {
            LabelType.ALL -> imageEmbedDao.findByLabel(query)
            LabelType.TEXT -> imageEmbedDao.findByTextEmbedding(query)
            LabelType.IMAGE -> imageEmbedDao.findByVisualEmbedding(query)
        }

        return imageEmbeddingRelations
            .parMap(context = dispatcher) { it.imageEntity.toExtraction() }
    }

    private fun List<Extraction>.filterByDateRange(range: DateRange?) = when {
        range != null -> this.filter { it.dateAdded isIn range }
        else -> this
    }
}