package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.data.ImageDataEntity
import com.drbrosdev.extractor.domain.model.MediaImage
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

interface Extractor {
    suspend fun run(mediaImage: MediaImage)
}

class DefaultExtractor(
    private val labelExtractor: ImageLabelExtractor<InputImage>,
    private val textExtractor: TextExtractor<InputImage>,
    private val provider: InputImageProvider,
    private val dispatcher: CoroutineDispatcher,
    private val imageDataDao: ImageDataDao
) : Extractor {
    override suspend fun run(mediaImage: MediaImage) {
        withContext(dispatcher) {
            val inputImage = provider.create(InputImageType.UriInputImage(mediaImage.uri))
            val text = async { textExtractor.run(inputImage) }
            val labels = async { labelExtractor.run(inputImage) }

            val result = awaitAll(text, labels)
                .joinToString { " " }
                .lowercase()

            val imageEntity = ImageDataEntity(
                mediaStoreId = mediaImage.id,
                uri = mediaImage.uri.toString(),
                labels = result
            )

            imageDataDao.insert(imageEntity)
        }
    }
}