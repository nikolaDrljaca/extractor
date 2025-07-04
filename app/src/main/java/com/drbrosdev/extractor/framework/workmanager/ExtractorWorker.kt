package com.drbrosdev.extractor.framework.workmanager

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import com.drbrosdev.extractor.domain.usecase.extractor.StartExtraction
import com.drbrosdev.extractor.framework.logger.logErrorEvent
import com.drbrosdev.extractor.framework.mlkit.MlKitMediaPipeInferenceService
import com.drbrosdev.extractor.framework.notification.NotificationService
import com.drbrosdev.extractor.framework.requiresApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/*
ExtractorWorker can be a KoinComponent since it is a part of the framework layer.
 */
class ExtractorWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        try {
            setForeground(getForegroundInfo())
        } catch (e: Exception) {
            logErrorEvent(
                message = "Starting Foreground Service failed.",
                throwable = e
            )
        }
        // using resource here will handle CancellationExceptions and other Fatal exceptions properly
        resourceScope {
            // create inference service
            val inferenceService = resource(
                acquire = {
                    MlKitMediaPipeInferenceService(
                        dispatcher = Dispatchers.Default, // is the same one CoroutineWorker uses
                        context = applicationContext
                    )
                },
                release = { it, _ -> it.close() }
            ).bind()
            val startExtraction: StartExtraction = get { parametersOf(inferenceService) }
            startExtraction.execute()
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo(
            notificationId = NotificationService.PROGRESS_ID,
            notification = notificationService.extractionInProgressNotification(NotificationService.PROGRESS_CHANNEL_ID) {
                it.setContentInfo("Extraction is running")
            }
        )
    }

    private fun createForegroundInfo(
        notificationId: Int,
        notification: Notification
    ): ForegroundInfo {
        return requiresApi(
            versionCode = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
            fallback = {
                ForegroundInfo(notificationId, notification)
            },
            block = {
                ForegroundInfo(
                    notificationId,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            }
        )
    }
}