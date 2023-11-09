package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.util.runCatching
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface Extractor {
    suspend fun execute(mediaImage: MediaImage)
}

class DefaultExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val labelExtractor: ImageLabelExtractor<InputImage>,
    private val textExtractor: TextExtractor<InputImage>,
    private val provider: InputImageProvider,
    private val extractorEntityDao: ExtractionEntityDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao
) : Extractor {

    override suspend fun execute(mediaImage: MediaImage) {
        withContext(dispatcher) {
            extractorEntityDao.insert(
                ExtractionEntity(
                    mediaStoreId = mediaImage.id,
                    uri = mediaImage.uri.toString()
                )
            )

            val inputImage = provider.create(InputImageType.UriInputImage(mediaImage.uri))

            val text: Deferred<Result<String>> = async {
                runCatching {
                    textExtractor.execute(inputImage).lowercase()
                }
            }

            val labels: Deferred<Result<List<String>>> = async {
                runCatching {
                    labelExtractor.execute(inputImage)
                }
            }

            val outText = text.await()
            val outLabel = labels.await()

            val textEmbeddingEntity = TextEmbeddingEntity(
                imageEntityId = mediaImage.id,
                value = outText.getOrDefault("")
            )
            textEmbeddingDao.insert(textEmbeddingEntity)

            outLabel
                .getOrDefault(emptyList())
                .map { VisualEmbeddingEntity(imageEntityId = mediaImage.id, value = it) }
                .forEach { visualEmbeddingDao.insert(it) }

        }
    }
}