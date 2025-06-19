package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageAnnotations
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.repository.payload.NewLupaImage
import kotlinx.coroutines.flow.Flow

interface LupaImageRepository {

    suspend fun deleteLupaImage(mediaImageId: Long)

    suspend fun deleteExtractionDataAndSearchIndex()

    suspend fun getAllIds(): Set<Long>

    fun findImageDataByMediaId(mediaImageId: MediaImageId): Flow<LupaImageAnnotations?>

    suspend fun updateTextEmbed(embedUpdate: EmbedUpdate)

    suspend fun updateUserEmbed(embedUpdate: List<EmbedUpdate>)

    suspend fun deleteVisualEmbed(mediaImageId: MediaImageId, value: String)

    suspend fun deleteUserEmbed(mediaImageId: MediaImageId, value: String)

    suspend fun createLupaImage(data: NewLupaImage)

    suspend fun getAllVisualEmbedValuesAsCsv(): String?

    suspend fun getAllTextEmbedValuesAsCsv(): String?

    fun getCountAsFlow(): Flow<Int>

    suspend fun getLatestLupaImage(): LupaImage?

    fun getLatestLupaImageAsFlow(): Flow<LupaImage>
}