package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.ImageEmbeddings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


interface ImageEmbeddingsRepository {

    suspend fun findByMediaId(mediaId: Long): Result<ImageEmbeddings>

    suspend fun updateEmbeddingsById(
        mediaId: Long,
        updatedImageEmbeddings: ImageEmbeddings
    )
}

class DefaultImageEmbeddingsRepository(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
) : ImageEmbeddingsRepository {

    override suspend fun findByMediaId(mediaId: Long): Result<ImageEmbeddings> {
        return withContext(dispatcher) {
            getEmbeddingsByMediaId(mediaId = mediaId)
        }
    }

    private suspend fun getEmbeddingsByMediaId(mediaId: Long) = runCatching {
        val textEmbedding = textEmbeddingDao.findByMediaId(mediaId)
        val userEmbedding = userEmbeddingDao.findByMediaId(mediaId)
        val visualEmbedding = visualEmbeddingDao.findByMediaId(mediaId)

        requireNotNull(textEmbedding) { "This should never happen." }

        ImageEmbeddings(
            textEmbedding = textEmbedding,
            visualEmbeddings = visualEmbedding,
            userEmbedding = userEmbedding
        )
    }

    override suspend fun updateEmbeddingsById(
        mediaId: Long,
        updatedImageEmbeddings: ImageEmbeddings
    ) = withContext(dispatcher) {
        launch {
            updatedImageEmbeddings.visualEmbeddings.forEach {
                visualEmbeddingDao.update(it)
            }
        }

        updatedImageEmbeddings.userEmbedding?.let {
            userEmbeddingDao.update(it)
        }

        textEmbeddingDao.update(updatedImageEmbeddings.textEmbedding)
    }
}