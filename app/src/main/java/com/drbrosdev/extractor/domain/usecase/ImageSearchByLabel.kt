package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.util.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

enum class LabelType {
    ALL,
    TEXT,
    IMAGE
}

interface ImageSearchByLabel {

    suspend fun search(query: String, labelType: LabelType): List<MediaImage>
}


class DefaultImageSearchByLabel(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
    private val insertPreviousSearch: InsertPreviousSearch
) : ImageSearchByLabel {

    override suspend fun search(query: String, labelType: LabelType) = withContext(dispatcher) {
        val out = when (labelType) {
            LabelType.ALL -> findAllByAll(query)
            LabelType.TEXT -> findAllByText(query)
            LabelType.IMAGE -> findAllByVisual(query)
        }

        runCatching { insertPreviousSearch(query, out.size, labelType) }
        out
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
