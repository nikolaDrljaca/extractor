package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.worker.ExtractorWorkerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CompleteOnboarding(
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: ExtractorDataStore,
    private val workerService: ExtractorWorkerService
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        // allocate initial searches
        dataStore.incrementSearchCountBy(amount = INITIAL_AMOUNT)
        // start indexing
        workerService.startExtractorWorker()
        // mark onboarding as finished
        dataStore.finishOnboarding()
    }

    companion object {
        const val INITIAL_AMOUNT = 1000
    }
}