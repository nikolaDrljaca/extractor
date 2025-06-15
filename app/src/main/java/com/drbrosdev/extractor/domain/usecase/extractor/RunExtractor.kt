package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

class RunExtractor(
    private val inferenceService: InferenceService,
    private val tokenizeText: TokenizeText = TokenizeText(Dispatchers.Default)
) {

    suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds? {
        return coroutineScope {
            // NOTE: inferenceService calls are main-safe
            // create media image data - prepare for extraction
            val inputImage = inferenceService.prepareImage(mediaImageUri)
                .getOrNull() ?: return@coroutineScope null
            // extract text data - async
            val textData = async { extractTextData(inputImage) }
                .await()
            // extract visual data - async
            val visualData = async { extractVisualData(inputImage) }
                .await()
            // return embeds
            ImageEmbeds(
                textEmbed = textData,
                visualEmbeds = visualData,
                userEmbeds = emptyList()
            )
        }
    }

    private suspend fun extractTextData(inputImage: MediaImageData): Embed.Text {
        return inferenceService.processText(inputImage)
            .fold(
                onFailure = { Embed.Text.DEFAULT },
                onSuccess = { result ->
                    val clean = tokenizeText.invoke(result.lowercase())
                        .toList()
                        .joinToString(separator = " ") { token -> token.text }
                    Embed.Text(clean)
                }
            )
    }

    private suspend fun extractVisualData(inputImage: MediaImageData): List<Embed.Visual> {
        return inferenceService.processVisual(inputImage)
            .fold(
                onFailure = { emptyList() },
                onSuccess = { result ->
                    result.distinct()
                        .map { label -> Embed.Visual(label) }
                }
            )
    }
}
