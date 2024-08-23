package com.drbrosdev.extractor.framework.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.requiresApi

class NotificationService(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Create an extraction in progress notification object.
     * @param block Use to configure or override default values on the notification.
     */
    fun extractionInProgressNotification(
        channelId: String,
        block: (NotificationCompat.Builder) -> Unit = {}
    ): Notification {
        val title = "Extraction is running."
        val name = "Extractor Progress Channel"

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.extractor_icon)
            .setOngoing(true)

        requiresApi(Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, name)
        }

        block(builder)

        return builder.build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int? = null
    ): NotificationChannel {
        val import = importance ?: NotificationManager.IMPORTANCE_LOW

        return NotificationChannel(channelId, channelName, import).also {
            notificationManager.createNotificationChannel(it)
        }
    }

    companion object {
        const val PROGRESS_CHANNEL_ID = "extractor_progress_channel_id"
        const val PROGRESS_ID = 1
    }
}