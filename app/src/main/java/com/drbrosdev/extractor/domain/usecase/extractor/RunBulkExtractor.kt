package com.drbrosdev.extractor.domain.usecase.extractor

import arrow.fx.coroutines.parMapUnordered
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.util.CONCURRENCY
import com.drbrosdev.extractor.framework.logger.logError
import com.drbrosdev.extractor.framework.logger.logInfo
import com.drbrosdev.extractor.util.mediaImageUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn

class RunBulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaStoreImageRepository,
    private val runExtractor: RunExtractor,
    private val extractorRepository: ExtractorRepository
) {
    suspend fun execute() {
        val storedIds = extractorRepository.getAllIds()
        val onDeviceIds = mediaImageRepository.getAllIds()

        val isOnDevice = onDeviceIds.subtract(storedIds)
        val isInStorage = storedIds.subtract(onDeviceIds)

        if (isOnDevice == isInStorage) return


        val mediaImages = mediaImageRepository.findAllById(onDeviceIds.toList())
            .associateBy { it.mediaImageId }

        when {
            isOnDevice.size > isInStorage.size -> {
                // perform extraction
                logInfo("Processing extraction for ${isOnDevice.size} images from device.")
                isOnDevice.asFlow()
                    .parMapUnordered(CONCURRENCY) {
                        //NOTE: Watch for the throw
                        val mediaStoreImage = mediaImages[it]!!
                        val embeds = runExtractor.execute(mediaStoreImage.mediaImageUri())
                            .onFailure { exception ->
                                logError(
                                    "Extraction failed for image",
                                    exception
                                )
                            }
                            .getOrThrow()

                        val data = NewExtraction(
                            mediaImageId = MediaImageId(it),
                            extractorImageUri = MediaImageUri(mediaStoreImage.uri.toString()),
                            path = mediaStoreImage.path,
                            dateAdded = mediaStoreImage.dateAdded,
                            textEmbed = embeds.textEmbed,
                            visualEmbeds = embeds.visualEmbeds
                        )
                        data
                    }
                    .flowOn(dispatcher)
                    .collect { data ->
                        extractorRepository.createExtractionData(data)
                    }
            }

            isOnDevice.size < isInStorage.size -> {
                //delete diff
                isInStorage
                    .asFlow()
                    .collect {
                        extractorRepository.deleteExtractionData(it)
                    }
            }

            else -> Unit
        }
    }
}