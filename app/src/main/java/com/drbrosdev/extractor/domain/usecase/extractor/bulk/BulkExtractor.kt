package com.drbrosdev.extractor.domain.usecase.extractor.bulk

import com.drbrosdev.extractor.data.repository.ExtractorDataRepository
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val extractor: Extractor,
    private val extractorDataRepository: ExtractorDataRepository
) {
    suspend fun execute() {
        val storedIds = extractorDataRepository.getAllIds()
        val onDeviceIds = mediaImageRepository.getAllIds()
        println(onDeviceIds.size)

        val isOnDevice = onDeviceIds.subtract(storedIds)
        val isInStorage = storedIds.subtract(onDeviceIds)

        if (isOnDevice == isInStorage) return

        val threads = Runtime.getRuntime().availableProcessors()
        val mediaImages = toMap(mediaImageRepository.findAllById(onDeviceIds.toList()))

        withContext(dispatcher) {
            val chunks = when {
                isOnDevice.isNotEmpty() -> isOnDevice.chunked(isOnDevice.size / threads)
                else -> emptyList()
            }
            chunks.forEach { chk ->
                launch {
                    //For each image only on device, I need to run extraction
                    chk.forEach {
                        val mediaImage = mediaImages[it]!!
                        extractor.execute(mediaImage)
                    }
                }
            }

            launch {
                isInStorage.forEach {
                    extractorDataRepository.deleteExtractionData(imageEntityId = it)
                }
            }
        }
    }

    private fun toMap(items: List<MediaImage>): Map<Long, MediaImage> {
        val out = mutableMapOf<Long, MediaImage>()
        items.forEach {
            out[it.mediaImageId] = it
        }
        return out
    }
}