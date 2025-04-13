package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService

class CreateMediaImageData(
    private val inferenceService: InferenceService
) {
    suspend fun execute(mediaImageUri: MediaImageUri): MediaImageData? {
        return inferenceService.prepareImage(mediaImageUri)
            .getOrNull()
    }
}