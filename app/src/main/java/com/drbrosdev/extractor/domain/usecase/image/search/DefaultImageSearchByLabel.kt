package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.model.isIn
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.InsertPreviousSearch
import com.drbrosdev.extractor.util.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultImageSearchByLabel(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
    private val insertPreviousSearch: InsertPreviousSearch
) : ImageSearchByLabel {

    private val searches = mutableMapOf<ImageSearchByLabel.Params, List<MediaImage>>()

    override suspend fun search(params: ImageSearchByLabel.Params): List<MediaImage> =
        searches.getOrPut(params) {
            withContext(dispatcher) {
                val out = when (params.labelType) {
                    LabelType.ALL -> findAllByAll(params.query)
                    LabelType.TEXT -> findAllByText(params.query)
                    LabelType.IMAGE -> findAllByVisual(params.query)
                }

                runCatching {
                    if (params.query.isNotBlank()) insertPreviousSearch(
                        params.query,
                        out.size,
                        params.labelType
                    )
                }

                when {
                    params.dateRange != null -> out.filter { it.dateAdded isIn params.dateRange }
                    else -> out
                }
            }
        }

    private fun processQueryIntoLabels(query: String): List<String> {
        if (query.isBlank()) return emptyList()

        val temp = query
            .trim()
            .replace(Regex("\\s+"), " ")
            .lowercase()

        return temp.split(" ")
    }

    private suspend fun findAllByAll(query: String): List<MediaImage> {
        val labels = processQueryIntoLabels(query)
        val result = mutableSetOf<MediaImage>()

        for (label in labels) {
            val ids = imageDataWithEmbeddingsDao
                .findByLabel(label)
                .map { it.imageEntity.mediaStoreId }
            if (ids.isEmpty()) continue

            val mediaImages = mediaImageRepository.findAllById(ids)
            result.addAll(mediaImages)
        }

        return result.toList()
    }

    private suspend fun findAllByText(query: String): List<MediaImage> {
        val labels = processQueryIntoLabels(query)
        val result = mutableSetOf<MediaImage>()

        for (label in labels) {
            val ids = imageDataWithEmbeddingsDao
                .findByTextEmbedding(label)
                .map { it.imageEntity.mediaStoreId }
            if (ids.isEmpty()) continue

            val mediaImages = mediaImageRepository.findAllById(ids)
            result.addAll(mediaImages)
        }

        return result.toList()
    }


    private suspend fun findAllByVisual(query: String): List<MediaImage> {
        val labels = processQueryIntoLabels(query)
        val result = mutableSetOf<MediaImage>()

        for (label in labels) {
            val ids = imageDataWithEmbeddingsDao
                .findByVisualEmbedding(label)
                .map { it.imageEntity.mediaStoreId }
            if (ids.isEmpty()) continue

            val mediaImages = mediaImageRepository.findAllById(ids)
            result.addAll(mediaImages)
        }

        return result.toList()
    }
}
