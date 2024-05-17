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
        spawnExtractorWork.invoke()

        withContext(dispatcher) {
            dataStore.incrementSearchCountBy(amount = 100)
            dataStore.finishOnboarding()
        }
    }
}