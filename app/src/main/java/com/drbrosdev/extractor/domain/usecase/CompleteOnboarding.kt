package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CompleteOnboarding(
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: ExtractorDataStore,
    private val spawnExtractorWork: SpawnExtractorWork
){
    suspend operator fun invoke() {
        spawnExtractorWork.invoke()

        withContext(dispatcher) {
            dataStore.incrementSearchCount(amount = 100)
            dataStore.finishOnboarding()
        }
    }
}