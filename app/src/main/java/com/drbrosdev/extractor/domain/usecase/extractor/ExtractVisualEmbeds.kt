package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService

class ExtractVisualEmbeds(
    private val inferenceService: InferenceService
) {
    suspend fun execute(image: MediaImageUri): List<Embed.Visual> {
        return inferenceService.processVisual(image)
            .fold(
                onSuccess = {
                    it.map { label -> Embed.Visual(label) }
                },
                onFailure = { emptyList() }
            )
    }
}
