package com.drbrosdev.extractor.ui.onboarding.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.usecase.BulkExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartWorkerViewModel(
    private val workManager: WorkManager,
    private val datastore: ExtractorDataStore,
    private val bulkExtractor: BulkExtractor
) : ViewModel() {

    fun spawnWorkRequest() {
//        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
//            .build()
//
//        workManager.enqueueUniqueWork(
//            WorkNames.EXTRACTOR_WORK,
//            ExistingWorkPolicy.KEEP,
//            extractorWorkRequest
//        )
//
        viewModelScope.launch {
            datastore.finishOnboarding()
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bulkExtractor.execute()
            }
        }
    }
}