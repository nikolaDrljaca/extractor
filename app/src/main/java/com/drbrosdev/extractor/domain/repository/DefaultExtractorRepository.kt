package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.data.TransactionProvider
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.SearchIndexDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.SearchIndexEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.util.toImageEmbeds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionDao: ExtractionDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val searchIndexDao: SearchIndexDao,
    private val txRunner: TransactionProvider
) : ExtractorRepository {

    override suspend fun deleteExtractionData(mediaImageId: Long) {
        val countDeleted = extractionDao.deleteByMediaId(mediaImageId)
        if (countDeleted == 0) return

        visualEmbeddingDao.deleteByMediaId(mediaImageId)
        textEmbeddingDao.deleteByMediaId(mediaImageId)
        userEmbeddingDao.deleteByMediaId(mediaImageId)
        // update search index
        searchIndexDao.deleteByMediaId(mediaImageId)
    }

    override suspend fun deleteExtractionDataAndSearchIndex() {
        txRunner.withTransaction {
            searchIndexDao.deleteAll()
            userEmbeddingDao.deleteAll()
            visualEmbeddingDao.deleteAll()
            textEmbeddingDao.deleteAll()
            extractionDao.deleteAll()
        }
    }

    override suspend fun getAllIds(): Set<Long> {
        return extractionDao.findAllIds().toSet()
    }

    override fun findImageDataByMediaId(mediaImageId: MediaImageId): Flow<ImageEmbeds?> {
        return imageEmbeddingsDao.findByMediaImageId(mediaImageId.id)
            .distinctUntilChanged()
            .map { it?.toImageEmbeds() }
    }

    override suspend fun deleteUserEmbed(mediaImageId: MediaImageId, value: String) {
        userEmbeddingDao.findByMediaId(mediaImageId.id)?.let { userEmbed ->
            val updated = withContext(dispatcher) {
                userEmbed.value
                    .split(",")
                    .map { it.trim() }
                    .filter { it.lowercase() != value.lowercase() }
                    .joinToString(separator = ",") { it }
            }

            userEmbeddingDao.update(userEmbed.copy(value = updated))
            // update search index
            searchIndexDao.updateUserIndex(updated, userEmbed.extractionEntityId)
        }
    }

    override suspend fun updateTextEmbed(embedUpdate: EmbedUpdate) = with(embedUpdate) {
        textEmbeddingDao.update(value, mediaImageId.id)
        // update search index
        searchIndexDao.updateTextIndex(value, mediaImageId.id)
    }

    override suspend fun updateUserEmbed(embedUpdate: List<EmbedUpdate>) {
        // check if input is empty, and that only 1 media ID is in the list
        if (embedUpdate.map { it.mediaImageId.id }.toSet().size != 1) return

        val mediaId = embedUpdate.first().mediaImageId.id
        userEmbeddingDao.findByMediaId(mediaId)?.let { embedding ->
            val value = embedUpdate
                .map { it.value.trim() }
                .distinct() // Make sure each user keyword is unique for a certain image
                .joinToString(separator = ",") { it }

            userEmbeddingDao.update(embedding.copy(value = value))
            // update search index
            searchIndexDao.updateUserIndex(value, mediaId)
        }
    }

    override suspend fun deleteVisualEmbed(mediaImageId: MediaImageId, value: String) {
        visualEmbeddingDao.findByMediaId(mediaImageId.id)?.let { visualEmbed ->
            val updated = withContext(dispatcher) {
                visualEmbed.value
                    .split(",")
                    .map { it.trim() }
                    .filter { it.lowercase() != value.lowercase() }
                    .joinToString(separator = ",") { it }
            }

            visualEmbeddingDao.update(visualEmbed.copy(value = updated))
            // update search index
            searchIndexDao.updateVisualIndex(updated, visualEmbed.extractionEntityId)
        }
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

        // handle visual embeds
        val visuals = visualEmbeds
            .filter { it.value.isNotBlank() }
            .map { it.value.lowercase() }
            .joinToString(separator = ",") { it }

        val visualEntity = VisualEmbeddingEntity(
            extractionEntityId = mediaImageId.id,
            value = visuals
        )

        // Create default empty user entity
        val userEntity = UserEmbeddingEntity(
            extractionEntityId = mediaImageId.id,
            value = ""
        )

        val searchIndex = SearchIndexEntity(
            textIndex = textEmbed.value,
            visualIndex = visuals,
            userIndex = "", // Empty on first creation
            extractionEntityId = mediaImageId.id
        )

        txRunner.withTransaction {
            extractionDao.insert(extractionEntity)
            textEmbeddingDao.insert(textEntity)
            visualEmbeddingDao.insert(visualEntity)
            userEmbeddingDao.insert(userEntity)
            // create search index
            searchIndexDao.insert(searchIndex)
        }
    }
}