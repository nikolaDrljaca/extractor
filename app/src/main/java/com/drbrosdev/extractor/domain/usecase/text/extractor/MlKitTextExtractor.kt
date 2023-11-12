package com.drbrosdev.extractor.domain.usecase.text.extractor

import com.drbrosdev.extractor.util.runCatching
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MlKitTextExtractor(
    private val dispatcher: CoroutineDispatcher
) : TextExtractor<InputImage> {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    override suspend fun execute(image: InputImage): Result<String> {
        return withContext(dispatcher) {
            val out = runCatching {
                textRecognizer.process(image).await()
            }
            out.mapCatching { it.text.lowercase() }
        }
    }
}
