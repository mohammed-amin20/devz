package com.mohamed.devz

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mohamed.devz.feature.core.data.data_source.remote.DevzFirebaseMessagingService
import com.mohamed.devz.navigation.DevzNavHost
import com.mohamed.devz.ui.theme.DevzTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showPendingNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pendingQuestionId = intent?.getIntExtra("questionId", 0)?.takeIf { it > 0 }

        if (intent?.getBooleanExtra(DevzFirebaseMessagingService.EXTRA_REQUEST_PERMISSION, false) == true) {
            requestNotificationPermission()
        }

        enableEdgeToEdge()
        setContent {
            DevzTheme {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    DevzNavHost(
                        pendingQuestionId = pendingQuestionId,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(DevzFirebaseMessagingService.EXTRA_REQUEST_PERMISSION, false)) {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showPendingNotification()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPendingNotification() {
        val title = intent?.getStringExtra(DevzFirebaseMessagingService.EXTRA_NOTIFICATION_TITLE)
            ?: return
        val body = intent?.getStringExtra(DevzFirebaseMessagingService.EXTRA_NOTIFICATION_BODY)
            ?: return
        val questionId = intent?.getIntExtra("questionId", 0)?.takeIf { it > 0 }

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            questionId?.let { putExtra("questionId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, DevzFirebaseMessagingService.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}