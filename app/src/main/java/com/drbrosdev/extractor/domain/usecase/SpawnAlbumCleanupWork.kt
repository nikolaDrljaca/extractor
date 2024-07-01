package com.drbrosdev.extractor.domain.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.drbrosdev.extractor.domain.worker.AlbumCleanupWorker
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.domain.worker.WorkerDataKeys
import com.drbrosdev.extractor.framework.requiresApi

class SpawnAlbumCleanupWork(
    private val workManager: WorkManager
) {

    operator fun invoke(albumId: Long) {
        val workRequest = OneTimeWorkRequestBuilder<AlbumCleanupWorker>()
            .setInputData(
                workDataOf(
                    WorkerDataKeys.ALBUM_ID to albumId
                )
            )

        requiresApi(
            apiLevel = 31,
            fallback = {
                workManager.enqueueUniqueWork(
                    WorkNames.ALBUM_CLEANUP,
                    ExistingWorkPolicy.APPEND,
                    workRequest.build()
                )
            }
        ) {
            workManager.enqueueUniqueWork(
                WorkNames.ALBUM_CLEANUP,
                ExistingWorkPolicy.APPEND,
                workRequest
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
            )
        }
    }
}