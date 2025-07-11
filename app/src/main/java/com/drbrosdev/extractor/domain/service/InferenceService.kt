package com.drbrosdev.extractor.domain.service

import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri

interface InferenceService: AutoCloseable {

    suspend fun processText(image: MediaImageData): Result<String>

    suspend fun processVisual(image: MediaImageData): Result<List<String>>

    suspend fun prepareImage(uri: MediaImageUri): Result<MediaImageData>

    suspend fun processDescription(image: MediaImageData): Result<String>

    suspend fun isImageDescriptorAvailable(): Boolean
}