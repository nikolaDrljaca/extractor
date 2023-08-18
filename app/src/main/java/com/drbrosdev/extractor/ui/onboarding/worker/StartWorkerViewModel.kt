package com.drbrosdev.extractor.ui.onboarding.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.launch

class StartWorkerViewModel(
    private val workManager: WorkManager,
    private val datastore: ExtractorDataStore
) : ViewModel() {

    fun spawnWorkRequest() {
        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
            .build()

        workManager.enqueueUniqueWork(
            WorkNames.EXTRACTOR_WORK,
            ExistingWorkPolicy.KEEP,
            extractorWorkRequest
        )

        viewModelScope.launch {
            datastore.finishOnboarding()
        }
    }
}