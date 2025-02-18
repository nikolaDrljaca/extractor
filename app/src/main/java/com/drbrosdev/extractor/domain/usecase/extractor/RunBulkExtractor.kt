package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.mediaImageId
import com.drbrosdev.extractor.domain.model.mediaImageUri
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

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

        when {
            isOnDevice.size > isInStorage.size -> {
                // perform extraction
                logEvent("Processing extraction for ${isOnDevice.size} images from device.")
                mediaImageRepository.findAllByIdAsFlow(isOnDevice.toList())
                    .map { mediaStoreImage ->
                        val embeds = runExtractor.execute(mediaStoreImage.mediaImageUri())
                        NewExtraction(
                            mediaImageId = mediaStoreImage.mediaImageId(),
                            extractorImageUri = mediaStoreImage.mediaImageUri(),
                            path = mediaStoreImage.path,
                            dateAdded = mediaStoreImage.dateAdded,
                            textEmbed = embeds?.textEmbed ?: Embed.defaultTextEmbed,
                            visualEmbeds = embeds?.visualEmbeds ?: listOf()
                        )
                    }
                    .flowOn(dispatcher)
                    .collect {
                        extractorRepository.createExtractionData(it)
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