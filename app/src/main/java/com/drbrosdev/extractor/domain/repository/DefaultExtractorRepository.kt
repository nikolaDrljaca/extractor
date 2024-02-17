package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.data.TransactionProvider
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.repository.payload.NewEmbed
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.util.mapToImageEmbeds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionDao: ExtractionDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val txRunner: TransactionProvider
) : ExtractorRepository {

    override suspend fun deleteExtractionData(mediaImageId: Long) {
        val countDeleted = extractionDao.deleteByMediaId(mediaImageId)
        if (countDeleted == 0) return

        visualEmbeddingDao.deleteByMediaId(mediaImageId)
        textEmbeddingDao.deleteByMediaId(mediaImageId)
        userEmbeddingDao.deleteByMediaId(mediaImageId)
    }

    override suspend fun getAllIds(): Set<Long> {
        return extractionDao.findAllIds().toSet()
    }

    override fun findImageDataByMediaId(mediaImageId: MediaImageId): Flow<ImageEmbeds?> {
        return imageEmbeddingsDao.findByMediaImageId(mediaImageId.id)
            .distinctUntilChanged()
            .map { it?.mapToImageEmbeds() }
    }

    override suspend fun updateTextEmbed(embedUpdate: EmbedUpdate) = with(embedUpdate) {
        textEmbeddingDao.update(value, mediaImageId.id)
    }

    override suspend fun updateOrInsertUserEmbed(embedUpdate: EmbedUpdate) {
        val existing = userEmbeddingDao.findByMediaId(embedUpdate.mediaImageId.id)
        if (existing == null) {
            val newUserEmbed = UserEmbeddingEntity(extractionEntityId = embedUpdate.mediaImageId.id, value = embedUpdate.value)
            userEmbeddingDao.insert(newUserEmbed)
        } else {
            userEmbeddingDao.update(embedUpdate.value, embedUpdate.mediaImageId.id)
        }
    }

    override suspend fun deleteVisualEmbed(visualEmbeddingId: Long) {
        visualEmbeddingDao.deleteById(visualEmbeddingId)
    }

    override suspend fun insertVisualEmbed(newEmbed: NewEmbed) = with(newEmbed) {
        val visualEmbed = VisualEmbeddingEntity(
            extractionEntityId = mediaImageId.id,
            value = value
        )
        visualEmbeddingDao.insert(visualEmbed)
    }

    override suspend fun deleteVisualEmbed(mediaImageId: MediaImageId, value: String) {
        visualEmbeddingDao.findByMediaId(mediaImageId.id)
            .find { it.value == value }
            ?.let { visualEmbeddingDao.delete(it) }
    }

    override suspend fun createExtractionData(data: NewExtraction) = with(data) {
        val extractionEntity = ExtractionEntity(
            mediaStoreId = mediaImageId.id,
            uri = extractorImageUri.uri,
            dateAdded = dateAdded,
            path = path
        )

        val textEntity = TextEmbeddingEntity(
            extractionEntityId = mediaImageId.id,
            value = textEmbed.value
        )

        val visualEntities = visualEmbeds.map {
            VisualEmbeddingEntity(
                extractionEntityId = mediaImageId.id,
                value = it.value
            )
        }

        //NOTE: Ordering is important for relationships
        txRunner.withTransaction {
            extractionDao.insert(extractionEntity)
            textEmbeddingDao.insert(textEntity)
            visualEmbeddingDao.insertAll(visualEntities)
        }
    }

    override suspend fun createExtractionData(data: List<NewExtraction>) {
        val extractionEntities = data.map {
            ExtractionEntity(
                mediaStoreId = it.mediaImageId.id,
                uri = it.extractorImageUri.uri,
                dateAdded = it.dateAdded,
                path = it.path
            )
        }

        val textEmbeds = data.map {
            TextEmbeddingEntity(
                extractionEntityId = it.mediaImageId.id,
                value = it.textEmbed.value
            )
        }

        val visualEmbeds = data.flatMap { create ->
            create.visualEmbeds.map {
                VisualEmbeddingEntity(
                    extractionEntityId = create.mediaImageId.id,
                    value = it.value
                )
            }
        }

        txRunner.withTransaction {
            textEmbeddingDao.insertAll(textEmbeds)
            extractionDao.insertAll(extractionEntities)
            visualEmbeddingDao.insertAll(visualEmbeds)
        }
    }
}