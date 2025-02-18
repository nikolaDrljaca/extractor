package com.drbrosdev.extractor.domain.usecase.extractor

import arrow.core.Either
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.usecase.image.create.CreateInputImage
import com.drbrosdev.extractor.domain.usecase.label.extractor.ExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.text.extractor.ExtractTextEmbed
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class DefaultRunExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val createInputImage: CreateInputImage,
    private val extractTextEmbed: ExtractTextEmbed<InputImage>,
    private val extractVisualEmbeds: ExtractVisualEmbeds<InputImage>,
    private val mediaPipeExtractVisualEmbeds: ExtractVisualEmbeds<InputImage>,
) : RunExtractor {
    override suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds? {
        return withContext(dispatcher) {
            val inputImage =
                createInputImage.execute(InputImageType.UriInputImage(mediaImageUri.toUri()))

            inputImage.onLeft {
                logErrorEvent("Failed to create InputImage.")
            }

            when (inputImage) {
                is Either.Left -> null
                is Either.Right -> extractFrom(inputImage.value)
            }
        }
    }

    private suspend fun extractFrom(inputImage: InputImage): ImageEmbeds = coroutineScope {
        val text = async {
            extractTextEmbed.execute(inputImage)
        }

        val visuals = async {
            extractVisualEmbeds.execute(inputImage)
        }

        val mediaPipeVisuals = async {
            mediaPipeExtractVisualEmbeds.execute(inputImage)
        }

        val outText = text.await().getOrDefault(Embed.defaultTextEmbed)

        val outVisualEmbeds = visuals.await().getOrDefault(emptyList())
            .asSequence()
            .plus(
                mediaPipeVisuals.await().getOrDefault(emptyList())
                    .asSequence()
            )
            .distinctBy { it.value.lowercase() }
            .toList()

        ImageEmbeds(
            textEmbed = outText,
            visualEmbeds = outVisualEmbeds,
            userEmbeds = emptyList()
        )
    }
}
