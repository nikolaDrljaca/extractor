package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.LupaImageAnnotations
import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

class ExtractLupaAnnotations(
    private val inferenceService: InferenceService,
    /*
    passing in a constant here allows the descriptor check to be run only once
    it might not be cheap to do it for every `execute` call.
    Also, initializing it lazily and storing as a piece of state does not sound preferable.
     */
    private val isDescriptorAvailable: Boolean = true,
    private val tokenizeText: TokenizeText = TokenizeText(Dispatchers.Default)
) {
    suspend fun execute(mediaImageUri: MediaImageUri): LupaImageAnnotations? {
        return coroutineScope {
            // NOTE: inferenceService calls are main-safe
            // create media image data - prepare for extraction
            val inputImage = inferenceService.prepareImage(mediaImageUri)
                .getOrNull() ?: return@coroutineScope null
            // extract text data
            val textData = async { extractTextData(inputImage) }
                .await()
            // extract visual data
            val visualData = async { extractVisualData(inputImage) }
                .await()
            // extract description
            val description = when {
                isDescriptorAvailable -> async { extractDescription(inputImage) }.await()
                else -> ""
            }
            // return embeds
            LupaImageAnnotations(
                textEmbed = textData,
                descriptionEmbed = description,
                visualEmbeds = visualData,
                userEmbeds = emptyList()
            )
        }
    }

    private suspend fun extractDescription(inputImage: MediaImageData): String {
        return inferenceService.processDescription(inputImage)
            .fold(
                onFailure = { "" },
                onSuccess = { result ->
                    // results here should not need sanitation
                    result
                },
            )
    }

    private suspend fun extractTextData(inputImage: MediaImageData): String {
        return inferenceService.processText(inputImage)
            .fold(
                onFailure = { "" },
                onSuccess = { result ->
                    val clean = tokenizeText.invoke(result.lowercase())
                        .toList()
                        .joinToString(separator = " ") { token -> token.text }
                    clean
                }
            )
    }

    private suspend fun extractVisualData(inputImage: MediaImageData): List<String> {
        return inferenceService.processVisual(inputImage)
            .fold(
                onFailure = { emptyList() },
                onSuccess = { result ->
                    result.distinct()
                        .map { label -> label }
                }
            )
    }
}
