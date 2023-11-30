package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.payload.CreateExtraction
import com.drbrosdev.extractor.data.payload.EmbedUpdate
import com.drbrosdev.extractor.data.payload.NewEmbed
import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import kotlinx.coroutines.flow.Flow

interface ExtractorRepository {

    suspend fun deleteExtractionData(imageEntityId: Long)

    fun getAll(): Flow<List<ExtractionEntity>>

    suspend fun getAllIds(): Set<Long>

    fun findImageDataByMediaId(mediaImageId: Long): Flow<ImageDataWithEmbeddings?>

    suspend fun updateTextEmbed(embedUpdate: EmbedUpdate)

    suspend fun updateOrInsertUserEmbed(embedUpdate: EmbedUpdate)

    suspend fun insertVisualEmbedding(newEmbed: NewEmbed)

    suspend fun deleteVisualEmbedding(visualEmbeddingId: Long)

    suspend fun createExtractionData(data: CreateExtraction)

}