package com.mohamed.devz.feature.core.data.data_source.remote

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mohamed.devz.MainActivity
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class DevzFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val title = message.notification?.title ?: data["title"] ?: "DevZ"
        val body = message.notification?.body ?: data["body"] ?: data["message"] ?: ""
        val questionId = data["questionId"]?.toIntOrNull()

        showNotification(title, body, questionId)
    }

    private fun saveToken(token: String) {
        runBlocking {
            val accountId =
                userPreferencesRepository.observeCurrentAccountId().first() ?: return@runBlocking
            if (accountId == 0) return@runBlocking
            when (val result = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val updated = result.data.copy(fcmToken = token)
                    accountRepository.update(updated)
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun showNotification(title: String, body: String, questionId: Int?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_REQUEST_PERMISSION, true)
                putExtra(EXTRA_NOTIFICATION_TITLE, title)
                putExtra(EXTRA_NOTIFICATION_BODY, body)
                questionId?.let { putExtra("questionId", it) }
            }
            startActivity(intent)
            return
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            questionId?.let { putExtra("questionId", it) }
        }

        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(
                System.currentTimeMillis().toInt(),
                notification
            )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "DevZ_Notifications"
        const val EXTRA_REQUEST_PERMISSION = "request_notification_permission"
        const val EXTRA_NOTIFICATION_TITLE = "pending_notification_title"
        const val EXTRA_NOTIFICATION_BODY = "pending_notification_body"
    }
}