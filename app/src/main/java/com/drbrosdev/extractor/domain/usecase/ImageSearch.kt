package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.util.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


interface ImageSearch {

    /**
     * Searches database embeds using query and returns a list of [MediaImage].
     * Performed against User, Text and Visual embeddings.
     * NO support for any permutation of the three.
     *
     * Note: Expects a non-blank string as input.
     */
    suspend fun execute(query: String): Result<List<MediaImage>>

}

class DefaultImageSearch(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
    private val insertPreviousSearch: InsertPreviousSearch
) : ImageSearch {


    override suspend fun execute(query: String): Result<List<MediaImage>> {
        if (query.isBlank()) return Result.failure(Throwable("Query must not be blank."))

        return withContext(dispatcher) {
            val temp = query
                .trim()
                .replace(Regex("\\s+"), " ")
                .lowercase()

            val labels = temp.split(" ")
            val foundImages = mutableSetOf<MediaImage>()
            for (label in labels) {
                val ids = imageDataWithEmbeddingsDao
                    .findByLabel(query = label)
                    .map { it.imageEntity.mediaStoreId }
                if(ids.isEmpty()) continue

                val mediaImages = mediaImageRepository.findAllById(ids)
                foundImages.addAll(mediaImages)
            }

            val out = foundImages.toList()
            runCatching { insertPreviousSearch(temp, out.size, LabelType.ALL) }

            Result.success(out)
        }
    }
}

enum class SearchStrategy {
    NORMAL,
    DIRTY_CHECKING
}
