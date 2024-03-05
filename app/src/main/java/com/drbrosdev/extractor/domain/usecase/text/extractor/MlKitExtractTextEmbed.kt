package com.drbrosdev.extractor.domain.usecase.text.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.util.runCatching
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MlKitExtractTextEmbed(
    private val dispatcher: CoroutineDispatcher,
    private val tokenizeText: TokenizeText,
) : ExtractTextEmbed<InputImage> {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun execute(image: InputImage): Result<Embed.Text> {
        return withContext(dispatcher) {
            val out = runCatching {
                textRecognizer.process(image).await()
            }

            out.mapCatching {
                val clean = tokenizeText(it.text.lowercase())
                    .toList()
                    .joinToString(separator = " ") { token -> token.text }

                Embed.Text(clean)
            }
        }
    }
}
