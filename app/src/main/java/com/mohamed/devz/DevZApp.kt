package com.mohamed.devz

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.mohamed.devz.feature.core.data.data_source.remote.DevzFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DevZApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DevzFirebaseMessagingService.NOTIFICATION_CHANNEL_ID,
                "DevZ Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for questions, answers, and likes"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}