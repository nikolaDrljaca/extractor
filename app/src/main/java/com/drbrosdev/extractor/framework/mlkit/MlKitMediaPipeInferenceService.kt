package com.drbrosdev.extractor.framework.mlkit

import android.content.Context
import arrow.core.raise.result
import com.drbrosdev.extractor.domain.model.MediaImageData
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.framework.logger.logEvent
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
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
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MlKitMediaPipeInferenceService(
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : InferenceService {
    // text extraction client
    private val textRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // image labels client
    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()
    private val imageLabeler = ImageLabeling.getClient(options)

    // mediaPipe classifier client
    private val imageClassifierOptions = ImageClassifier.ImageClassifierOptions.builder()
        .setBaseOptions(
            BaseOptions.builder()
                .setModelAssetPath(MODEL_PATH)
                .build()
        )
        .setRunningMode(RunningMode.IMAGE)
        .setMaxResults(5)
        .build()
    private val imageClassifier = ImageClassifier.createFromOptions(
        context,
        imageClassifierOptions
    )

    // image descriptions client
    private val imageDescriberOptions = ImageDescriberOptions.builder(context)
        .build()
    private val imageDescriber by lazy {
        ImageDescription.getClient(imageDescriberOptions)
    }

    // TODO @drljacan review `result` related code - mapCatching will catch CancellationExceptions!
    // TODO all exceptions other than CancellationExceptions need to be handled here
    // TODO Use either Either.catch or effect {} from arrow - they only handle NonFatal Exceptions

    override suspend fun processText(image: MediaImageData): Result<String> =
        withContext(dispatcher) {
            // parse inputImage
            val internal = result {
                when {
                    image is MlKitImageData -> image.inputImage
                    else -> error("MediaImageData implementation does not contain InputImage!")
                }
            }
            // process
            internal
                .mapCatching { textRecognizer.process(it).await() }
                .map { it.text }
        }

    override suspend fun processVisual(image: MediaImageData): Result<List<String>> =
        withContext(dispatcher) {
            // parse inputImage
            val internal = result {
                when {
                    image is MlKitImageData -> image.inputImage
                    else -> error("MediaImageData implementation does not contain InputImage!")
                }
            }
            internal.mapCatching { inputImage ->
                // prepare mlKit result
                val mlKitResult = async {
                    imageLabeler.process(inputImage).await().map { it.text }
                }
                // prepare mediaPipe result
                val mediaPipeResult = async { runMediaPipe(inputImage) }

                mlKitResult.await()
                    .plus(mediaPipeResult.await())
            }
        }

    override suspend fun prepareImage(uri: MediaImageUri): Result<MediaImageData> {
        return result {
            InputImage.fromFilePath(context, uri.toUri())
                .toImageData()
        }
    }

    // TODO provide override
    suspend fun isImageDescriptorAvailable() = withContext(dispatcher) {
        imageDescriber.checkFeatureStatus().await() == FeatureStatus.AVAILABLE
    }

    override fun close() {
        textRecognizer.close()
        imageLabeler.close()
        imageClassifier.close()
        imageDescriber.close()
    }

    // TODO wire in
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

    // TODO Rip this model out completely
    private fun runMediaPipe(image: InputImage): List<String> {
        val bitmap = image.bitmapInternal ?: return emptyList()
        val result =
            imageClassifier.classify(BitmapImageBuilder(bitmap).build())

        return result.classificationResult()
            .classifications()
            .flatMap { it.categories() }
            .filter { it.score().times(1_000f) >= THRESHOLD }
            .map { it.categoryName() }
    }

    companion object {
        private const val MODEL_PATH = "efficientnet_lite0.tflite"
        private const val THRESHOLD = 90f
    }
}