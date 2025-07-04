package com.drbrosdev.extractor.data.extraction

import com.drbrosdev.extractor.data.TransactionProvider
import com.drbrosdev.extractor.data.extraction.dao.DescriptionEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.ExtractionDao
import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.extraction.record.DescriptionEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.LupaImageMetadataRecord
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.relation.toLupaAnnotations
import com.drbrosdev.extractor.data.extraction.relation.toLupaImage
import com.drbrosdev.extractor.data.search.SearchIndexDao
import com.drbrosdev.extractor.data.search.SearchIndexRecord
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageAnnotations
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class DefaultLupaImageRepository(
    private val extractionDao: ExtractionDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val descriptionEmbeddingDao: DescriptionEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val searchIndexDao: SearchIndexDao,
    private val txRunner: TransactionProvider
) : LupaImageRepository {

    override suspend fun deleteLupaImage(mediaImageId: Long) {
        val countDeleted = extractionDao.deleteByMediaId(mediaImageId)
        if (countDeleted == 0) return

        visualEmbeddingDao.deleteByMediaId(mediaImageId)
        textEmbeddingDao.deleteByMediaId(mediaImageId)
        userEmbeddingDao.deleteByMediaId(mediaImageId)
        descriptionEmbeddingDao.deleteByLupaImageId(mediaImageId)
        // update search index
        searchIndexDao.deleteByMediaId(mediaImageId)
    }

    override suspend fun deleteExtractionDataAndSearchIndex() {
        txRunner.withTransaction {
            searchIndexDao.deleteAll()
            userEmbeddingDao.deleteAll()
            visualEmbeddingDao.deleteAll()
            textEmbeddingDao.deleteAll()
            descriptionEmbeddingDao.deleteAll()
            extractionDao.deleteAll()
        }
    }

    override suspend fun getAllIds(): Set<Long> {
        return extractionDao.findAllIds().toSet()
    }

    override fun findImageDataByMediaId(mediaImageId: MediaImageId): Flow<LupaImageAnnotations?> {
        return imageEmbeddingsDao.findByMediaImageId(mediaImageId.id)
            .distinctUntilChanged()
            .map { it?.toLupaAnnotations() }
    }

    override suspend fun findImageByUri(uri: MediaImageUri): LupaImage? {
        return imageEmbeddingsDao.findByUri(uri.uri)?.toLupaImage()
    }

    override fun findByIdAsFlow(mediaImageId: MediaImageId): Flow<LupaImage?> {
        return imageEmbeddingsDao.findByMediaImageId(mediaImageId.id)
            .map { it?.toLupaImage() }
    }

    override suspend fun getAllVisualEmbedValuesAsCsv(): String? {
        return visualEmbeddingDao.findAllVisualEmbedValues()
    }

    override suspend fun getAllTextEmbedValuesAsCsv(): String? {
        return textEmbeddingDao.findAllTextEmbedValues()
    }

    override fun getCountAsFlow(): Flow<Int> {
        return extractionDao.getCountAsFlow()
    }

    override suspend fun getLatestLupaImage(): LupaImage? {
        return imageEmbeddingsDao.findMostRecent()?.toLupaImage()
    }

    override fun getLatestLupaImageAsFlow(): Flow<LupaImage> {
        return imageEmbeddingsDao.findMostRecentAsFlow()
            .filterNotNull()
            .map { it.toLupaImage() }
    }

    override suspend fun deleteUserEmbed(mediaImageId: MediaImageId, value: String) {
        userEmbeddingDao.findByMediaId(mediaImageId.id)?.let { userEmbed ->
            val updated = userEmbed.value
                .split(",")
                .map { it.trim() }
                .filter { it.lowercase() != value.lowercase() }
                .joinToString(separator = VisualEmbeddingRecord.SEPARATOR) { it }

            userEmbeddingDao.update(userEmbed.copy(value = updated))
            // update search index
            searchIndexDao.updateUserIndex(updated, userEmbed.lupaImageId)
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
            val updated = visualEmbed.value
                .split(",")
                .map { it.trim() }
                .filter { it.lowercase() != value.lowercase() }
                .joinToString(separator = VisualEmbeddingRecord.SEPARATOR) { it }

            visualEmbeddingDao.update(visualEmbed.copy(value = updated))
            // update search index
            searchIndexDao.updateVisualIndex(updated, visualEmbed.lupaImageId)
        }
    }

    override suspend fun createLupaImage(data: LupaImage) = with(data) {
        // extract data
        val mediaImageId = metadata.mediaImageId
        val textEmbed = annotations.textEmbed
        val visualEmbeds = annotations.visualEmbeds
        val descriptionEmbed = annotations.descriptionEmbed

        val lupaImageMetadataRecord = LupaImageMetadataRecord(
            mediaStoreId = metadata.mediaImageId.id,
            uri = metadata.uri.uri,
            dateAdded = metadata.dateAdded,
            path = metadata.path
        )
        // handle text embed
        val textEntity = TextEmbeddingRecord(
            lupaImageId = mediaImageId.id,
            value = textEmbed
        )
        // handle visual embeds
        val visuals = visualEmbeds
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .joinToString(separator = VisualEmbeddingRecord.SEPARATOR) { it }
        val visualEntity = VisualEmbeddingRecord(
            lupaImageId = mediaImageId.id,
            value = visuals
        )
        // handle description embed
        val descriptionEntity = DescriptionEmbeddingRecord(
            lupaImageId = mediaImageId.id,
            value = descriptionEmbed
        )

        // handle user entity - Create default empty user entity
        val userEntity = UserEmbeddingRecord(
            lupaImageId = mediaImageId.id,
            value = ""
        )

        val searchIndex = SearchIndexRecord(
            textIndex = textEmbed,
            descriptionIndex = descriptionEmbed,
            visualIndex = visuals,
            userIndex = "", // Empty on first creation
            extractionId = mediaImageId.id
        )

        txRunner.withTransaction {
            extractionDao.insert(lupaImageMetadataRecord)
            textEmbeddingDao.insert(textEntity)
            descriptionEmbeddingDao.insert(descriptionEntity)
            visualEmbeddingDao.insert(visualEntity)
            userEmbeddingDao.insert(userEntity)
            // create search index
            searchIndexDao.insert(searchIndex)
        }
    }
}