package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


interface ImageSearch {

    /**
     * Searches database embeds using query and returns a list of [MediaImage].
     *
     * Note: Expects a non-blank string as input.
     */
    suspend fun execute(query: String): Result<List<MediaImage>>

}

class DefaultImageSearch(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataDao: ImageDataDao
) : ImageSearch {


    override suspend fun execute(query: String): Result<List<MediaImage>> {
        if (query.isBlank()) return Result.failure(Throwable("Query must not be blank."))

        return withContext(dispatcher) {
            val temp = query
                .trim()
                .replace(Regex("\\s+"), " ")
                .lowercase()

            val labels = temp.split(" ")
            val out = mutableSetOf<MediaImage>()
            for (label in labels) {
                val ids = imageDataDao.findByLabel(label.lowercase())
                    .first()
                    .map { it.mediaStoreId }
                if (ids.isEmpty()) continue

                val mediaImages = mediaImageRepository.findAllById(ids)
                out.addAll(mediaImages)
            }

            Result.success(out.toList())
        }
    }
}
