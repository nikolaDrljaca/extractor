package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.RememberSearch
import com.drbrosdev.extractor.util.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultImageSearchByLabel(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
    private val rememberSearch: RememberSearch
) : ImageSearchByLabel {

    override suspend fun search(params: ImageSearchByLabel.Params): List<MediaImage> =
        withContext(dispatcher) {
            val result = with(params) {
                val embedSupplier: suspend (String) -> List<ImageDataWithEmbeddings> =
                    when (labelType) {
                        LabelType.ALL -> { it -> imageDataWithEmbeddingsDao.findByLabel(it) }
                        LabelType.TEXT -> { it -> imageDataWithEmbeddingsDao.findByTextEmbedding(it) }
                        LabelType.IMAGE -> { it ->
                            imageDataWithEmbeddingsDao.findByVisualEmbedding(it)
                        }
                    }

                query
                    .prepareQuery()
                    .findBy { embedSupplier(it) }
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

    private suspend fun String.findBy(block: suspend (String) -> List<ImageDataWithEmbeddings>): List<Long> {
        return block(this)
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