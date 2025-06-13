package com.drbrosdev.extractor.framework.mlkit

import android.content.Context
import arrow.core.raise.ensure
import arrow.core.raise.result
import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.framework.logger.logEvent
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.imagedescription.ImageDescriptionRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MlKitMediaPipeInferenceService(
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : InferenceService {
    // text extraction client
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // image labels client
    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()
    private val imageLabeler = ImageLabeling.getClient(options)

    // image descriptions client
    private val imageDescriberOptions = ImageDescriberOptions.builder(context)
        .build()
    private val imageDescriber = ImageDescription.getClient(imageDescriberOptions)

    // NOTE: using Arrow.result {} makes sure CancellationExceptions are propagated
    // and other fatal exceptions are NOT caught (JvmOutOfMemory etc)

    override suspend fun processText(image: MediaImageData): Result<String> =
        withContext(dispatcher) {
            result {
                val internal = extractInputImage(image).bind()
                textRecognizer.process(internal)
                    .await()
                    .text
            }
        }

    override suspend fun processVisual(image: MediaImageData): Result<List<String>> =
        withContext(dispatcher) {
            result {
                val internal = extractInputImage(image).bind()
                imageLabeler.process(internal)
                    .await()
                    .map { it.text }
            }
        }

    override suspend fun processDescription(image: MediaImageData): Result<String> =
        withContext(dispatcher) {
            result {
                val input = extractInputImage(image).bind()
                runImageDescriber(input)
            }
        }

    override suspend fun prepareImage(uri: MediaImageUri): Result<MediaImageData> {
        return result {
            InputImage.fromFilePath(context, uri.toUri())
                .toImageData()
        }
    }

    override suspend fun isImageDescriptorAvailable() = withContext(dispatcher) {
        imageDescriber.checkFeatureStatus().await() == FeatureStatus.AVAILABLE
    }

    override fun close() {
        textRecognizer.close()
        imageLabeler.close()
        imageDescriber.close()
    }

    private fun extractInputImage(image: MediaImageData) = result {
        ensure(image is MlKitImageData) {
            error("MediaImageData implementation does not contain InputImage!")
        }
        image.inputImage
    }

    private suspend fun runImageDescriber(image: InputImage): String {
        val featureStatus = imageDescriber.checkFeatureStatus()
            .await()
        when (featureStatus) {
            FeatureStatus.DOWNLOADING -> logEvent("Gemini Nano model features are downloading.")
            FeatureStatus.DOWNLOADABLE -> logEvent("Gemini Nano model features are downloadable.")
            FeatureStatus.UNAVAILABLE -> logEvent("Gemini Nano model is not available for device.")
        }
        if (featureStatus == FeatureStatus.UNAVAILABLE) {
            return ""
        }
        // create request
        val bitmap = image.bitmapInternal ?: return ""
        val request = ImageDescriptionRequest.builder(bitmap)
            .build()
        // running first inference will trigger model/features download - can take a while
        val imageDescription = imageDescriber.runInference(request)
            .await()
        return imageDescription.description
    }
}