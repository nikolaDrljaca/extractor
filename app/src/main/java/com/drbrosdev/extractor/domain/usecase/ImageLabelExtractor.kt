package com.drbrosdev.extractor.domain.usecase

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface ImageLabelExtractor<T> {
    suspend fun execute(image: T): String
}

class MLKitImageLabelExtractor(
    private val dispatcher: CoroutineDispatcher
): ImageLabelExtractor<InputImage> {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    override suspend fun execute(image: InputImage): String {
        return withContext(dispatcher) {
            labeler.process(image).await()
                .joinToString(" ") { it.text }
        }
    }
}