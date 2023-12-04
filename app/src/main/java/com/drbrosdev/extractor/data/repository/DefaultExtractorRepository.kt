package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.data.payload.CreateExtraction
import com.drbrosdev.extractor.data.payload.EmbedUpdate
import com.drbrosdev.extractor.data.payload.NewEmbed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionEntityDao: ExtractionEntityDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
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
        imageEmbeddingsDao.findByMediaImageId(mediaImageId)

    override suspend fun updateTextEmbed(embedUpdate: EmbedUpdate) = with(embedUpdate) {
        textEmbeddingDao.update(value, mediaImageId)
    }

    override suspend fun updateOrInsertUserEmbed(embedUpdate: EmbedUpdate) {
        val existing = userEmbeddingDao.findByMediaId(embedUpdate.mediaImageId)
        if (existing == null) {
            val newUserEmbed = UserEmbeddingEntity(imageEntityId = embedUpdate.mediaImageId, value = embedUpdate.value)
            userEmbeddingDao.insert(newUserEmbed)
        } else {
            userEmbeddingDao.update(embedUpdate.value, embedUpdate.mediaImageId)
        }
    }

    override suspend fun deleteVisualEmbedding(visualEmbeddingId: Long) {
        visualEmbeddingDao.deleteById(visualEmbeddingId)
    }

    override suspend fun insertVisualEmbedding(newEmbed: NewEmbed) = with(newEmbed) {
        val visualEmbed = VisualEmbeddingEntity(
            imageEntityId = mediaImageId,
            value = value
        )
        visualEmbeddingDao.insert(visualEmbed)
    }

    override suspend fun createExtractionData(data: CreateExtraction) = with(data) {
        val extractorEntity = ExtractionEntity(
            mediaStoreId = mediaImageId,
            uri = extractorImageUri
        )

        val textEntity = TextEmbeddingEntity(
            imageEntityId = mediaImageId,
            value = textEmbed
        )

        val visualEntities = visualEmbeds.map {
            VisualEmbeddingEntity(imageEntityId = mediaImageId, value = it)
        }

        //NOTE: Ordering is important for relationships
        extractionEntityDao.insert(extractorEntity)
        textEmbeddingDao.insert(textEntity)
        visualEmbeddingDao.insertAll(*visualEntities.toTypedArray())
    }
}