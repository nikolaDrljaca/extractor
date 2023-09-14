package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val extractor: Extractor,
    private val extractorRepository: ExtractorRepository
) {
    suspend fun execute() {
        val storedImages = extractorRepository.getAll().first()
        val deviceImages = mediaImageRepository.getAll()
        val deviceImagesMap = toMap(deviceImages)

        val storedIds = storedImages.map { it.mediaStoreId }
        val onDeviceIds = deviceImages.map { it.id }

        val isOnDevice = onDeviceIds.subtract(storedIds.toSet())
        val isInStorage = storedIds.subtract(onDeviceIds.toSet())

        if (isOnDevice == isInStorage) return

        val threads = Runtime.getRuntime().availableProcessors()

        withContext(dispatcher) {
            val chunks = isOnDevice.chunked(isOnDevice.size / threads)
            chunks.forEach { chk ->
                launch {
                    //For each image only on device, I need to run extraction
                    chk.forEach {
                        val mediaImage = deviceImagesMap[it]!!
                        extractor.execute(mediaImage)
                    }
                }
            }

            launch {
                isInStorage.forEach {
                    extractorRepository.deleteExtractionData(imageEntityId = it)
                }
            }
        }
    }

    private fun toMap(items: List<MediaImage>): Map<Long, MediaImage> {
        val out = mutableMapOf<Long, MediaImage>()
        items.forEach {
            out[it.id] = it
        }
        return out
    }
}