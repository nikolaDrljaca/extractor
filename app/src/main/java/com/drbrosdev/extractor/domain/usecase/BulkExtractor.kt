package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataDao: ImageDataDao,
    private val extractor: Extractor
) {
    suspend fun execute() {
        val storedImages = imageDataDao.getAll().first()
        val deviceImages = mediaImageRepository.getAll()
        val deviceImagesMap = toMap(deviceImages)

        val storedIds = storedImages.map { it.mediaStoreId }
        val onDeviceIds = deviceImages.map { it.id }

        val isOnDevice = onDeviceIds.subtract(storedIds.toSet())
        val isInStorage = storedIds.subtract(onDeviceIds.toSet())

        if (isOnDevice == isInStorage) return

        val threads = Runtime.getRuntime().availableProcessors()

        withContext(dispatcher) {
            println("threads: $threads, whole ${Runtime.getRuntime().availableProcessors()}")
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
                //For each image only in storage, I need to delete them
                isInStorage.forEach {
                    imageDataDao.deleteByMediaId(it)
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