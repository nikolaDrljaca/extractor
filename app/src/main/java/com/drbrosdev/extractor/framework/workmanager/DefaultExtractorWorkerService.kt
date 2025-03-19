package com.drbrosdev.extractor.framework.workmanager

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.domain.service.WorkNames
import com.drbrosdev.extractor.domain.service.WorkerDataKeys
import com.drbrosdev.extractor.framework.requiresApi

class DefaultExtractorWorkerService(
    private val workManager: WorkManager
): ExtractorWorkerService {
    override fun startExtractorWorker() {
        val workRequest: OneTimeWorkRequest = requiresApi(
            versionCode = 31,
            fallback = { OneTimeWorkRequestBuilder<ExtractorWorker>().build() },
            block = {
                OneTimeWorkRequestBuilder<ExtractorWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
            }
        )
        workManager.enqueueUniqueWork(
            WorkNames.EXTRACTOR_WORK,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun startAlbumCleanupWorker(albumId: Long) {
        val workData = workDataOf(WorkerDataKeys.ALBUM_ID to albumId)
        val workRequest: OneTimeWorkRequest = requiresApi(
            versionCode = 31,
            fallback = {
                OneTimeWorkRequestBuilder<AlbumCleanupWorker>()
                    .setInputData(workData)
                    .build()
            },
            block = {
                OneTimeWorkRequestBuilder<AlbumCleanupWorker>()
                    .setInputData(workData)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
            }
        )
        workManager.enqueueUniqueWork(
            WorkNames.ALBUM_CLEANUP,
            ExistingWorkPolicy.APPEND,
            workRequest
        )
    }
}