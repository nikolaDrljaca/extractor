package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.data.payload.CreateExtraction
import com.drbrosdev.extractor.data.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.label.extractor.ImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.TextExtractor
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val labelExtractor: ImageLabelExtractor<InputImage>,
    private val textExtractor: TextExtractor<InputImage>,
    private val inputImageFactory: InputImageFactory,
    private val extractorRepository: ExtractorRepository
) : Extractor {

    override suspend fun execute(mediaImage: MediaImage) {
        withContext(dispatcher) {
            val inputImage = inputImageFactory.create(InputImageType.UriInputImage(mediaImage.uri))

            val text = async {
                textExtractor.execute(inputImage)
            }

            val labels = async {
                labelExtractor.execute(inputImage)
            }

            val outText = text.await()
            val outLabel = labels.await()

            val payload = CreateExtraction(
                mediaImageId = mediaImage.mediaImageId,
                extractorImageUri = mediaImage.uri.toString(),
                textEmbed = outText.getOrDefault(""),
                visualEmbeds = outLabel.getOrDefault(emptyList())
            )

            extractorRepository.createExtractionData(payload)
        }
    }
}
