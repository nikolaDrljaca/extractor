package com.drbrosdev.extractor.domain.usecase.image

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.service.AppReviewService
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.drbrosdev.extractor.framework.logger.logEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SearchImageSideEffects(
    private val dispatcher: CoroutineDispatcher,
    private val appReviewService: AppReviewService,
    private val datastore: ExtractorDataStore
) {
    private val reviewThreshold = 10

    suspend fun execute() = withContext(dispatcher) {
        // decrease search count
        datastore.decrementSearchCount()
        // get current count
        val currentCount = datastore.getSearchCount()
        // show in-app review
        if (currentCount % reviewThreshold == 0) {
            appReviewService.requestReview()
                .onSuccess { logEvent("Successfully launched in-app review flow at $currentCount searches.") }
                .onFailure { logErrorEvent("Failed to launch in-app review flow at $currentCount searches.") }
        }
    }
}