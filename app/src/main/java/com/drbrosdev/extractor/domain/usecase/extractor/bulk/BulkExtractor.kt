package com.drbrosdev.extractor.domain.usecase.extractor.bulk

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaImageRepository,
    private val extractor: Extractor,
    private val extractorDataRepository: ExtractorRepository
) {
    suspend fun execute() {
        val storedIds = extractorDataRepository.getAllIds()
        val onDeviceIds = mediaImageRepository.getAllIds()

        val isOnDevice = onDeviceIds.subtract(storedIds)
        val isInStorage = storedIds.subtract(onDeviceIds)

        if (isOnDevice == isInStorage) return

        val mediaImages = mediaImageRepository.findAllById(onDeviceIds.toList())
            .associateBy { it.mediaImageId }

        withContext(dispatcher) {
            when {
                isOnDevice.size > isInStorage.size -> {
                    //perform extraction
                    isOnDevice.parMap {
                        extractor.execute(mediaImages[it]!!)
                    }
                }

                isOnDevice.size < isInStorage.size -> {
                    //delete diff
                    isInStorage.parMap {
                        extractorDataRepository.deleteExtractionData(it)
                    }
                }

                else -> Unit
            }
        }
    }
}