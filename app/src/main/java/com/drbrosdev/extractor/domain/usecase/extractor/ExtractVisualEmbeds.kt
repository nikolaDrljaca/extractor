package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.service.InferenceService

class ExtractVisualEmbeds(
    private val inferenceService: InferenceService
) {
    suspend fun execute(image: MediaImageData): List<Embed.Visual> {
        return inferenceService.processVisual(image)
            .fold(
                onSuccess = {
                    it
                        .distinct()
                        .map { label -> Embed.Visual(label) }
                },
                onFailure = { emptyList() }
            )
    }
}
