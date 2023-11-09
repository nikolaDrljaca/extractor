package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ExtractorRepository {

    suspend fun deleteExtractionData(imageEntityId: Long)

    fun getAll(): Flow<List<ExtractionEntity>>

    suspend fun getAllIds(): Set<Long>

    fun findImageDataByMediaId(mediaImageId: Long): Flow<ImageDataWithEmbeddings?>

    suspend fun updateTextEmbed(value: String, imageEntityId: Long)

    suspend fun updateUserEmbed(value: String, imageEntityId: Long)

    suspend fun deleteVisualEmbedding(value: String)

    suspend fun insertVisualEmbedding(mediaImageId: Long, embed: String)
}

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionEntityDao: ExtractionEntityDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageDataWithEmbeddingsDao: ImageDataWithEmbeddingsDao,
) : ExtractorRepository {

    override suspend fun deleteExtractionData(imageEntityId: Long) {
        val countDeleted = extractionEntityDao.deleteByMediaId(imageEntityId)
        if (countDeleted == 0) return

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

    override suspend fun updateTextEmbed(value: String, imageEntityId: Long) {
        textEmbeddingDao.update(value, imageEntityId)
    }

    override suspend fun updateUserEmbed(value: String, imageEntityId: Long) {
        val existing = userEmbeddingDao.findByMediaId(imageEntityId)
        if (existing == null) {
            val newUserEmbed = UserEmbeddingEntity(imageEntityId = imageEntityId, value = value)
            userEmbeddingDao.insert(newUserEmbed)
        } else {
            userEmbeddingDao.update(value, imageEntityId)
        }
    }

    override suspend fun deleteVisualEmbedding(value: String) {
        visualEmbeddingDao.deleteByValue(value)
    }

    override suspend fun insertVisualEmbedding(mediaImageId: Long, embed: String) {
        val visualEmbed = VisualEmbeddingEntity(
            imageEntityId = mediaImageId,
            value = embed
        )
        visualEmbeddingDao.insert(visualEmbed)
    }

}