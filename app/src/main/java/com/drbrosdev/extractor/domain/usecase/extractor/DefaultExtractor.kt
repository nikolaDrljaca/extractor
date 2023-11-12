package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import com.drbrosdev.extractor.domain.model.InputImageType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.label.extractor.ImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.TextExtractor
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val labelExtractor: ImageLabelExtractor<InputImage>,
    private val textExtractor: TextExtractor<InputImage>,
    private val inputImageFactory: InputImageFactory,
    private val extractorEntityDao: ExtractionEntityDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao
) : Extractor {

    override suspend fun execute(mediaImage: MediaImage) {
        withContext(dispatcher) {
            extractorEntityDao.insert(
                ExtractionEntity(
                    mediaStoreId = mediaImage.mediaImageId,
                    uri = mediaImage.uri.toString()
                )
            )

            val inputImage = inputImageFactory.create(InputImageType.UriInputImage(mediaImage.uri))

            val text = async {
                textExtractor.execute(inputImage)
            }

            val labels = async {
                labelExtractor.execute(inputImage)
            }

            val outText = text.await()
            val outLabel = labels.await()

            val textEmbeddingEntity = TextEmbeddingEntity(
                imageEntityId = mediaImage.mediaImageId,
                value = outText.getOrDefault("")
            )
            textEmbeddingDao.insert(textEmbeddingEntity)

            outLabel
                .getOrDefault(emptyList())
                .map { VisualEmbeddingEntity(imageEntityId = mediaImage.mediaImageId, value = it) }
                .forEach { visualEmbeddingDao.insert(it) }

        }
    }
}
