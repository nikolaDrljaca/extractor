package com.drbrosdev.extractor.domain.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.domain.worker.WorkNames

class SpawnExtractorWork(
    private val workManager: WorkManager
) {

    operator fun invoke() {
        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
            .build()

        workManager.enqueueUniqueWork(
            WorkNames.EXTRACTOR_WORK,
            ExistingWorkPolicy.KEEP,
            extractorWorkRequest
        )
    }
}