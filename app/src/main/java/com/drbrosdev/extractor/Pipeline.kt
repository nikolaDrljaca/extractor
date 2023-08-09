package com.drbrosdev.extractor

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed interface ExtractorResult {
    data class Text(val text: String) : ExtractorResult
    data class Labels(val labels: List<String>) : ExtractorResult
}

fun Text.toExtractionResult(): ExtractorResult {
    return ExtractorResult.Text(
        text = this.text
    )
}


class Extractor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context
) {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    suspend fun run(imageUri: Uri) = withContext(dispatcher) {
        val image = InputImage.fromFilePath(context, imageUri)

        val textResult = async {
            textRecognizer.process(image).await().text
        }

        val labelResult = async {
            labeler.process(image).await()
                .filter { it.confidence >= 0.7f }
                .joinToString(" ") { it.text }
        }

        val out = awaitAll(textResult, labelResult).joinToString(" ")
        out
    }
}

data class ImageDescription(
    val mediaStoreId: Long,
    val uri: Uri,
    val labels: String
)

val allImageDescriptions = mutableListOf<ImageDescription>()

class PersistentExtractor(
    private val extractor: Extractor,
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend fun run() = withContext(dispatcher) {
        contentResolver.mediaImagesFlow()
            .first()
            .forEach { it ->
                println("Running extraction for image ${it.id}")
                val extractorResult = extractor.run(it.uri)

                val imageDescription = ImageDescription(
                    mediaStoreId = it.id,
                    uri = it.uri,
                    labels = extractorResult
                )
                allImageDescriptions.add(imageDescription)
            }
        allImageDescriptions.toList()
    }
}

