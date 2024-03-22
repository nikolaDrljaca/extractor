package com.drbrosdev.extractor.domain.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.RunBulkExtractor
import com.drbrosdev.extractor.framework.logger.logEvent
import com.drbrosdev.extractor.framework.notification.NotificationService
import com.drbrosdev.extractor.framework.requiresApi
import kotlin.time.measureTime

class ExtractorWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val extractor: RunBulkExtractor,
    private val mediaImageRepository: MediaStoreImageRepository,
    private val extractionDao: ExtractionDao,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setForeground(
            createForegroundInfo(
                NotificationService.PROGRESS_ID,
                notificationService.createNotification(NotificationService.PROGRESS_CHANNEL_ID) {
                    it.setContentInfo("Extraction is running")
                }
            )
        )

        val time = measureTime {
            extractor.execute()
        }

        val deviceImageCount = mediaImageRepository.getCount()
        val localImageCount = extractionDao.getCount()
        if (deviceImageCount != localImageCount) {
            return Result.retry()
        }

        logEvent("Extraction Worker processed $localImageCount images in ${time.inWholeMinutes}(minutes) - ${time.inWholeMilliseconds}(ms)")

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo(
            notificationId = NotificationService.PROGRESS_ID,
            notification = notificationService.createNotification(NotificationService.PROGRESS_CHANNEL_ID) {
                it.setContentInfo("Extraction is running")
            }
        )
    }

    private fun createForegroundInfo(
        notificationId: Int,
        notification: Notification
    ): ForegroundInfo {
        return requiresApi<ForegroundInfo>(
            versionCode = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
            fallback = {
                ForegroundInfo(notificationId, notification)
            },
            block = {
                ForegroundInfo(
                    notificationId,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            })
    }
}