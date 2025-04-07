package com.drbrosdev.extractor.domain.service

import com.drbrosdev.extractor.domain.model.MediaImageUri

interface InferenceService {

    suspend fun processText(image: MediaImageUri): Result<String>

    suspend fun processVisual(image: MediaImageUri): Result<List<String>>

}