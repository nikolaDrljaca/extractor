package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.usecase.image.create.CreateInputImage
import com.drbrosdev.extractor.domain.usecase.label.extractor.ExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.text.extractor.ExtractTextEmbed
import com.drbrosdev.extractor.util.runCatching
import com.drbrosdev.extractor.util.toUri
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultRunExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val extractVisualEmbeds: ExtractVisualEmbeds<InputImage>,
    private val extractTextEmbed: ExtractTextEmbed<InputImage>,
    private val createInputImage: CreateInputImage,
) : RunExtractor {

    override suspend fun execute(mediaImageUri: MediaImageUri): Result<ImageEmbeds> {
        return withContext(dispatcher) {
            val inputImage = runCatching {
                createInputImage.execute(InputImageType.UriInputImage(mediaImageUri.toUri()))
            }

            if (inputImage.isFailure) return@withContext Result.failure(
                inputImage.exceptionOrNull() ?: Throwable("Input image creation failed.")
            )

            val text = async {
                extractTextEmbed.execute(inputImage.getOrThrow())
            }

            val visuals = async {
                extractVisualEmbeds.execute(inputImage.getOrThrow())
            }

            val outText = text.await().getOrDefault(Embed.defaultTextEmbed)

            val outVisual = visuals.await().getOrDefault(emptyList())

            val out = ImageEmbeds(
                textEmbed = outText,
                visualEmbeds = outVisual,
                userEmbeds = emptyList()
            )

            Result.success(out)
        }
    }
}
