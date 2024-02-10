package com.drbrosdev.extractor.framework.notification

import android.app.Notification
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat

class NotificationServiceImpl: NotificationService {
    override fun createNotification(
        channelId: Int,
        builder: (NotificationCompat.Builder) -> Unit
    ): Notification {
        TODO("Not yet implemented")
    }

    override fun createNotificationChannel(
        channelId: Int,
        channelName: String,
        importance: Int?
    ): NotificationChannel {
        TODO("Not yet implemented")
    }
}