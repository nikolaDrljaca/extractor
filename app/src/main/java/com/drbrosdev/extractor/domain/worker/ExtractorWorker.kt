package com.drbrosdev.extractor.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor

class ExtractorWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val extractor: BulkExtractor,
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        extractor.execute()

        val deviceImageCount = mediaImageRepository.getCount()
        val localImageCount = extractionEntityDao.getCount()
        if (deviceImageCount != localImageCount) {
            return Result.retry()
        }

        return Result.success()
    }
}