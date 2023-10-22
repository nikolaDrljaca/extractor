package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.entity.UserEmbedding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ExtractorRepository {

    suspend fun deleteExtractionData(imageEntityId: Long)

    fun getAll(): Flow<List<ExtractionEntity>>

    suspend fun getAllIds(): Set<Long>

    fun findImageDataByMediaId(mediaImageId: Long): Flow<ImageDataWithEmbeddings?>

    suspend fun updateTextEmbed(value: String, imageEntityId: Long)

    suspend fun updateUserEmbed(value: String, imageEntityId: Long)
}

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionEntityDao: ExtractionEntityDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
) : ExtractorRepository {

    override suspend fun deleteExtractionData(imageEntityId: Long) = withContext(dispatcher) {
        val countDeleted = extractionEntityDao.deleteByMediaId(imageEntityId)
        if (countDeleted == 0) return@withContext

        visualEmbeddingDao.deleteByMediaId(imageEntityId)
        textEmbeddingDao.deleteByMediaId(imageEntityId)
        userEmbeddingDao.deleteByMediaId(imageEntityId)
    }

    override fun getAll(): Flow<List<ExtractionEntity>> {
        return extractionEntityDao.findAll()
    }

    override suspend fun getAllIds(): Set<Long> {
        return extractionEntityDao.findAllIds().toSet()
    }

    override fun findImageDataByMediaId(mediaImageId: Long) =
        imageDataWithEmbeddingsDao.findByMediaImageId(mediaImageId)

    override suspend fun updateTextEmbed(value: String, imageEntityId: Long) =
        withContext(dispatcher) {
            textEmbeddingDao.update(value, imageEntityId)
        }

    override suspend fun updateUserEmbed(value: String, imageEntityId: Long) {
        val existing = userEmbeddingDao.findByMediaId(imageEntityId)
        if (existing == null) {
            val newUserEmbed = UserEmbedding(imageEntityId = imageEntityId, value = value)
            userEmbeddingDao.insert(newUserEmbed)
        } else {
            userEmbeddingDao.update(value, imageEntityId)
        }
    }

}