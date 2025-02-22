package com.drbrosdev.extractor.domain.usecase.extractor.visual

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.drbrosdev.extractor.util.runCatching
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MLKitExtractVisualEmbeds(
    private val dispatcher: CoroutineDispatcher
) : ExtractVisualEmbeds<InputImage> {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    override suspend fun execute(image: InputImage): List<Embed.Visual> {
        return withContext(dispatcher) {
            runCatching { labeler.process(image).await() }
                .fold(
                    onSuccess = {
                        it.map { label ->
                            Embed.Visual(label.text)
                        }
                    },
                    onFailure = {
                        logErrorEvent("Failed to process image with MLKit image labeler.")
                        emptyList()
                    }
                )
        }
    }
}
