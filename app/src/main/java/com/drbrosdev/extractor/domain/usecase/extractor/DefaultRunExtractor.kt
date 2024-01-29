package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.usecase.image.create.CreateInputImage
import com.drbrosdev.extractor.domain.usecase.label.extractor.ExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.text.extractor.ExtractTextEmbed
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
            val inputImage = createInputImage.execute(InputImageType.UriInputImage(mediaImageUri.toUri()))

            val text = async {
                extractTextEmbed.execute(inputImage)
            }

            val labels = async {
                extractVisualEmbeds.execute(inputImage)
            }

            val outText = text.await().getOrDefault(Embed.defaultTextEmbed)
            val outVisual = labels.await().getOrDefault(listOf(Embed.defaultVisualEmbed))

            val out = ImageEmbeds(
                textEmbed = outText,
                visualEmbeds = outVisual,
                userEmbeds = null
            )

            Result.success(out)
        }
    }
}
