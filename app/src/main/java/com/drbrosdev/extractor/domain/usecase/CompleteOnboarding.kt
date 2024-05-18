package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ExtractorDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CompleteOnboarding(
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: ExtractorDataStore,
    private val spawnExtractorWork: SpawnExtractorWork
) {
    suspend operator fun invoke() {

        withContext(dispatcher) {
            // allocate initial searches
            dataStore.incrementSearchCountBy(amount = 100)
            // start indexing
            spawnExtractorWork.invoke()
            // mark onboarding as finished
            dataStore.finishOnboarding()
        }
    }
}