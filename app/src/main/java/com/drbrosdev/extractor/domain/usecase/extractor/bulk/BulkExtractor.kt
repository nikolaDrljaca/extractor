package com.drbrosdev.extractor.domain.usecase.extractor.bulk

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.payload.NewExtraction
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImageRepository
import com.drbrosdev.extractor.util.CONCURRENCY
import com.drbrosdev.extractor.util.mediaImageUri
import kotlinx.coroutines.CoroutineDispatcher

class BulkExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractor: Extractor,
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
                //perform extraction
                val result = isOnDevice.parMap(
                    concurrency = CONCURRENCY,
                    context = dispatcher
                ) {
                    //NOTE: Watch for the throw
                    val mediaStoreImage = mediaImages[it]!!
                    val embeds = extractor.execute(mediaStoreImage.mediaImageUri())
                        .getOrThrow()

                    NewExtraction(
                        mediaImageId = MediaImageId(it),
                        extractorImageUri = MediaImageUri(mediaStoreImage.uri.toString()),
                        path = mediaStoreImage.path,
                        dateAdded = mediaStoreImage.dateAdded,
                        textEmbed = embeds.textEmbed,
                        visualEmbeds = embeds.visualEmbeds
                    )
                }

                extractorRepository.createExtractionData(result)
            }

            isOnDevice.size < isInStorage.size -> {
                //delete diff
                isInStorage.parMap(concurrency = CONCURRENCY, context = dispatcher) {
                    extractorRepository.deleteExtractionData(it)
                }
            }

            else -> Unit
        }
    }
}