package com.drbrosdev.extractor.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.domain.usecase.extractor.BulkExtractor
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImageRepository

class ExtractorWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val extractor: BulkExtractor,
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractionDao: ExtractionDao
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        extractor.execute()

        val deviceImageCount = mediaImageRepository.getCount()
        val localImageCount = extractionDao.getCount()
        if (deviceImageCount != localImageCount) {
            return Result.retry()
        }

        return Result.success()
    }
}