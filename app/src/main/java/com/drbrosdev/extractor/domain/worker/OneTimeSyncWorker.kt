package com.drbrosdev.extractor.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.Extractor

class OneTimeSyncWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val extractor: Extractor,
    private val mediaImageRepository: MediaImageRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val images = mediaImageRepository.getAll()
        images.forEach {
            extractor.run(it)
        }

        return Result.success()
    }
}