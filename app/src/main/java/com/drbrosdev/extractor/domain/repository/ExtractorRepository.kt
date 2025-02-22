package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import kotlinx.coroutines.flow.Flow

interface ExtractorRepository {

    suspend fun deleteExtractionData(mediaImageId: Long)

    suspend fun deleteExtractionDataAndSearchIndex()

    suspend fun getAllIds(): Set<Long>

    fun findImageDataByMediaId(mediaImageId: MediaImageId): Flow<ImageEmbeds?>

    suspend fun updateTextEmbed(embedUpdate: EmbedUpdate)

    suspend fun updateUserEmbed(embedUpdate: List<EmbedUpdate>)

    suspend fun deleteVisualEmbed(mediaImageId: MediaImageId, value: String)

    suspend fun deleteUserEmbed(mediaImageId: MediaImageId, value: String)

    suspend fun createExtractionData(data: NewExtraction)

    suspend fun getAllVisualEmbedValuesAsCsv(): String?

    suspend fun getAllTextEmbedValuesAsCsv(): String?

    fun getExtractionCountAsFlow(): Flow<Int>
}