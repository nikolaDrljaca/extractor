package com.drbrosdev.extractor.domain.usecase.extractor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.flow.first
import kotlin.time.measureTime

object ExtractionNotInSync

class StartExtraction(
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractionRepository: ExtractorRepository,
    private val inferenceService: InferenceService
) {
    suspend fun execute(): Either<ExtractionNotInSync, Unit> = either {
        // check if gemini nano is available
        if (inferenceService.isImageDescriptorAvailable().not()) {
            logEvent("GeminiNano is not available on this system.")
        } else {
            logEvent("GeminiNano is might be available on this system.")
        }
        // create bulk extractor
        val extractor = BulkExtractLupaAnnotations(
            mediaImageRepository = mediaImageRepository,
            extractorRepository = extractionRepository,
            extractLupaAnnotations = ExtractLupaAnnotations(
                inferenceService = inferenceService
            )
        )
        // execute bulk extraction and measure time
        val time = measureTime {
            extractor.execute()
        }
        // retrieve counts
        val deviceImageCount = mediaImageRepository.getCount()
        val localImageCount = extractionRepository.getExtractionCountAsFlow()
            .first()
        // ensure extraction has indexed every image
        ensure(deviceImageCount == localImageCount) {
            ExtractionNotInSync
        }
        if (time.inWholeMinutes != 0L) {
            logEvent("Extraction Worker processed $localImageCount images in ${time.inWholeMinutes}(minutes) - ${time.inWholeMilliseconds}(ms)")
        }
        // success
        Unit
    }
}