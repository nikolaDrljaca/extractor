package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


interface ImageSearch {

    suspend fun execute(query: String): List<MediaImage>

}

class DefaultImageSearch(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataDao: ImageDataDao
) : ImageSearch {

    override suspend fun execute(query: String): List<MediaImage> {
        return withContext(dispatcher) {
            val labels = query.split(" ")
            val out = mutableSetOf<MediaImage>()
            for (label in labels) {
                val ids = imageDataDao.findByLabel(label.lowercase())
                    .first()
                    .map { it.mediaStoreId }
                if (ids.isEmpty()) continue

                val mediaImages = mediaImageRepository.findAllById(ids)
                out.addAll(mediaImages)
            }
            return@withContext out.toList()
        }
    }
}
