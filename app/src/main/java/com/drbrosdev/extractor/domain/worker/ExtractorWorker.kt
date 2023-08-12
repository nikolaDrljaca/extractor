package com.drbrosdev.extractor.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.domain.usecase.BulkExtractor

class ExtractorWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val extractor: BulkExtractor,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        extractor.execute()
        return Result.success()
    }
}