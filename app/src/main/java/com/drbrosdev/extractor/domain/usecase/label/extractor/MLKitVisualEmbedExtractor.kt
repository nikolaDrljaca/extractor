package com.drbrosdev.extractor.domain.usecase.label.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.util.runCatching
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MLKitVisualEmbedExtractor(
    private val dispatcher: CoroutineDispatcher
) : VisualEmbedExtractor<InputImage> {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    override suspend fun execute(image: InputImage): Result<List<Embed.Visual>> {
        return withContext(dispatcher) {
            val out = runCatching {
                labeler.process(image).await()
            }
            out.mapCatching {
                it.map { label ->
                    Embed.Visual(label.text)
                }
            }
        }
    }
}
