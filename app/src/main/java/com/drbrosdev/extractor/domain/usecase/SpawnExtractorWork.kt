package com.drbrosdev.extractor.domain.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.framework.requiresApi

class SpawnExtractorWork(
    private val workManager: WorkManager
) {
    operator fun invoke() {
        requiresApi(
            apiLevel = 31,
            fallback = {
                val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
                    .build()

                workManager.enqueueUniqueWork(
                    WorkNames.EXTRACTOR_WORK,
                    ExistingWorkPolicy.KEEP,
                    extractorWorkRequest
                )
            }
        ) {
            val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            workManager.enqueueUniqueWork(
                WorkNames.EXTRACTOR_WORK,
                ExistingWorkPolicy.KEEP,
                extractorWorkRequest
            )
        }
    }
}