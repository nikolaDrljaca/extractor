package com.drbrosdev.extractor.domain.usecase.extractor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.flow.first
import kotlin.time.measureTime

object ExtractionNotInSync

class StartExtraction(
    private val extractor: RunBulkExtractor,
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractionRepository: ExtractorRepository
) {
    suspend fun execute(): Either<ExtractionNotInSync, Unit> = either {
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