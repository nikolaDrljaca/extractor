package com.drbrosdev.extractor.domain.usecase.extractor

import arrow.fx.coroutines.parMapUnordered
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.drbrosdev.extractor.framework.logger.logEvent
import com.drbrosdev.extractor.util.CONCURRENCY
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
                logEvent("Processing extraction for ${isOnDevice.size} images from device.")
                isOnDevice.asFlow()
                    .parMapUnordered(CONCURRENCY) {
                        val mediaStoreImage = mediaImages[it]!!
                        val embeds = runExtractor.execute(mediaStoreImage.mediaImageUri())
                            .onFailure { exception ->
                                logErrorEvent(
                                    "Extraction failed for image",
                                    exception
                                )
                            }
                            .getOrNull()

                        val data = NewExtraction(
                            mediaImageId = MediaImageId(it),
                            extractorImageUri = MediaImageUri(mediaStoreImage.uri.toString()),
                            path = mediaStoreImage.path,
                            dateAdded = mediaStoreImage.dateAdded,
                            textEmbed = embeds?.textEmbed ?: Embed.defaultTextEmbed,
                            visualEmbeds = embeds?.visualEmbeds ?: listOf()
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