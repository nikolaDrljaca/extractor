package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.label.extractor.VisualEmbedExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.TextEmbedExtractor
import com.drbrosdev.extractor.util.toUri
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val visualEmbedExtractor: VisualEmbedExtractor<InputImage>,
    private val textEmbedExtractor: TextEmbedExtractor<InputImage>,
    private val inputImageFactory: InputImageFactory,
) : Extractor {

    override suspend fun execute(mediaImageUri: MediaImageUri): Result<ImageEmbeds> {
        return withContext(dispatcher) {
            val inputImage = inputImageFactory.create(InputImageType.UriInputImage(mediaImageUri.toUri()))

            val text = async {
                textEmbedExtractor.execute(inputImage)
            }

            val labels = async {
                visualEmbedExtractor.execute(inputImage)
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
