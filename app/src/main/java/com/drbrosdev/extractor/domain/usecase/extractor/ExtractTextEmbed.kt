package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.flow.toList

class ExtractTextEmbed(
    private val inferenceService: InferenceService,
    private val tokenizeText: TokenizeText,
) {
    suspend fun execute(image: MediaImageUri): Embed.Text {
        return inferenceService.processText(image)
            .fold(
                onSuccess = { result ->
                    val clean = tokenizeText.invoke(result.lowercase())
                        .toList()
                        .joinToString(separator = " ") { token -> token.text }

                    Embed.Text(clean)
                },
                onFailure = { Embed.Text.DEFAULT }
            )
    }
}
