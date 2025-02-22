package com.drbrosdev.extractor.domain.usecase.extractor.visual

import android.content.Context
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier.ImageClassifierOptions
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MediaPipeExtractVisualEmbeds(
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : ExtractVisualEmbeds<InputImage> {

    private val imageClassifierOptions = ImageClassifierOptions.builder()
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

    override suspend fun execute(image: InputImage) = withContext(dispatcher) {
        invoke(image)
    }

    private fun invoke(image: InputImage): List<Embed.Visual> {
        val bitmap = image.bitmapInternal ?: return emptyList()

        val mediaPipeImage = BitmapImageBuilder(bitmap).build()

        // classify is blocking so make sure to wrap in coroutine
        val result = runCatching {
            imageClassifier.classify(mediaPipeImage)
        }

        if (result.isFailure) {
            result.onFailure { logErrorEvent("Failed to process image with MediaPipe image classifier.") }
            return emptyList()
        }

        val out = result.getOrThrow()
            .classificationResult()
            .classifications()
            .asSequence()
            .flatMap { it.categories().asSequence() }
            // NOTE: category score values are between 0.1 and 0.00 -> times 1000 the range is 100 and 0
            .filter { it.score().times(1000f) >= THRESHOLD }
            .map { category ->
                Embed.Visual(
                    value = category.categoryName()
                )
            }
            .toList()

        return out
    }

    companion object {
        private const val MODEL_PATH = "efficientnet_lite0.tflite"
        private const val THRESHOLD = 90f
    }
}