package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.data.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.data.repository.ImageEmbeddingRepository
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.RememberSearch
import com.drbrosdev.extractor.util.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultImageSearchByLabel(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageEmbeddingRepository: ImageEmbeddingRepository,
    private val rememberSearch: RememberSearch
) : ImageSearchByLabel {

    override suspend fun search(params: ImageSearchByLabel.Params): List<MediaImage> =
        withContext(dispatcher) {
            val result = with(params) {
                query
                    .prepareQuery()
                    .mapToSearchStrategy(type)
                    .findBy(labelType)
                    .toMediaImage()
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

    private suspend fun ImageEmbeddingSearchStrategy.findBy(labelType: LabelType): List<Long> {
        val imageEmbeddingRelations = when (labelType) {
            LabelType.ALL -> imageEmbeddingRepository.findBy(this)
            LabelType.TEXT -> imageEmbeddingRepository.findByTextEmbed(this)
            LabelType.IMAGE -> imageEmbeddingRepository.findByVisualEmbed(this)
        }

        return imageEmbeddingRelations
            .map { it.imageEntity.mediaStoreId }
    }

    private suspend fun List<Long>.toMediaImage(): List<MediaImage> {
        return mediaImageRepository.findAllById(this)
    }

    private fun List<MediaImage>.filterByDateRange(range: DateRange?) = when {
        range != null -> this.filter { it.dateAdded isIn range }
        else -> this
    }
}