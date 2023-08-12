package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BulkExtractor(
    private val mediaImageRepository: MediaImageRepository,
    private val imageDataDao: ImageDataDao,
    private val extractor: Extractor
) {
    suspend fun execute() {
        val storedImages = imageDataDao.getAll().first()
        val deviceImages = mediaImageRepository.getAll()

        val storedIds = storedImages.map { it.mediaStoreId }
        val onDeviceIds = deviceImages.map { it.id }

        val isOnDevice = onDeviceIds.subtract(storedIds.toSet())
        val isInStorage = storedIds.subtract(onDeviceIds.toSet())

        if (isOnDevice == isInStorage) return

        coroutineScope {
            launch {
                //For each image only on device, I need to run extraction
                isOnDevice.forEach {
                    val mediaImage = deviceImages.find { img -> img.id == it }!!
                    extractor.execute(mediaImage)
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
}