package com.drbrosdev.extractor.framework.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.domain.usecase.album.CleanupAlbum
import com.drbrosdev.extractor.domain.service.WorkerDataKeys

class AlbumCleanupWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val cleanupAlbum: CleanupAlbum,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val albumId = inputData.getLong(WorkerDataKeys.ALBUM_ID, 0L)
        return cleanupAlbum.execute(albumId)
            .fold(
                ifLeft = { Result.failure() },
                ifRight = { Result.success() }
            )
    }
}