package com.drbrosdev.extractor.framework

import android.content.Context
import arrow.core.raise.result
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.service.InferenceService
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MlKitMediaPipeInferenceService(
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : InferenceService {
    private val textRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()
    private val labeler = ImageLabeling.getClient(options)

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

    override suspend fun processText(image: MediaImageUri): Result<String> =
        withContext(dispatcher) {
            result { createInputImage(image) }
                .mapCatching { textRecognizer.process(it).await() }
                .map { it.text }
        }

    override suspend fun processVisual(image: MediaImageUri): Result<List<String>> =
        withContext(dispatcher) {
            result {
                // prepare input image
                val inputImage = createInputImage(image)
                // prepare mlKit result
                val mlKitResult = async {
                    labeler.process(inputImage).await().map { it.text }
                }
                // prepare mediaPipe result
                val mediaPipeResult = async { runMediaPipe(inputImage) }

                mlKitResult.await()
                    .plus(mediaPipeResult.await())
            }
        }

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

    // Will throw if uri points to image not on device
    private fun createInputImage(uri: MediaImageUri): InputImage {
        return InputImage.fromFilePath(context, uri.toUri())
    }

    companion object {
        private const val MODEL_PATH = "efficientnet_lite0.tflite"
        private const val THRESHOLD = 90f
    }
}