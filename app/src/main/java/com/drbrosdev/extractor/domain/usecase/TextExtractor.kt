package com.drbrosdev.extractor.domain.usecase

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface TextExtractor<T> {
    suspend fun run(image: T): String
}

class MlKitTextExtractor(
    private val dispatcher: CoroutineDispatcher
) : TextExtractor<InputImage> {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    override suspend fun run(image: InputImage): String {
        return withContext(dispatcher) {
            textRecognizer.process(image).await().text
        }
    }
}