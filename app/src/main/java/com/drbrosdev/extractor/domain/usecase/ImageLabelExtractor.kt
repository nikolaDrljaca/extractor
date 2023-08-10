package com.drbrosdev.extractor.domain.usecase

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface ImageLabelExtractor<T> {
    suspend fun run(image: T): String
}

class MLKitImageLabelExtractor(
    private val dispatcher: CoroutineDispatcher
): ImageLabelExtractor<InputImage> {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    override suspend fun run(inputImage: InputImage): String {
        return withContext(dispatcher) {
            labeler.process(inputImage).await()
                .joinToString(" ") { it.text }
        }
    }
}