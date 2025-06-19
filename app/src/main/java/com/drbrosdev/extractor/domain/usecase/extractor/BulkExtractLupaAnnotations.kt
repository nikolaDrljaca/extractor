package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.mediaImageId
import com.drbrosdev.extractor.domain.model.mediaImageUri
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.repository.payload.NewLupaImage
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

class BulkExtractLupaAnnotations(
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractLupaAnnotations: ExtractLupaAnnotations,
    private val lupaImageRepository: LupaImageRepository
) {
    suspend fun execute() {
        val storedIds = lupaImageRepository.getAllIds()
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
                        val embeds =
                            extractLupaAnnotations.execute(mediaStoreImage.mediaImageUri())
                        NewLupaImage(
                            mediaImageId = mediaStoreImage.mediaImageId(),
                            extractorImageUri = mediaStoreImage.mediaImageUri(),
                            path = mediaStoreImage.path,
                            dateAdded = mediaStoreImage.dateAdded,
                            textEmbed = embeds?.textEmbed ?: "",
                            visualEmbeds = embeds?.visualEmbeds ?: emptyList()
                        )
                    }
                    .collect {
                        lupaImageRepository.createLupaImage(it)
                    }
            }

            isOnDevice.size < isInStorage.size -> {
                //delete diff
                isInStorage
                    .asFlow()
                    .collect {
                        lupaImageRepository.deleteLupaImage(it)
                    }
            }

            else -> Unit
        }
    }
}