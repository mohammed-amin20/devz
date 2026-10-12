package com.mohamed.devz

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.mohamed.devz.feature.core.data.data_source.remote.DevzFirebaseMessagingService
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.settings.presentation.AppLanguage
import com.mohamed.devz.navigation.DevzNavHost
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

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
        val pendingActorId = intent?.getIntExtra("actorId", 0)?.takeIf { it > 0 }
        val pendingJobId = intent?.getIntExtra("jobId", 0)?.takeIf { it > 0 }
        val pendingReportId = intent?.getIntExtra("reportId", 0)?.takeIf { it > 0 }

        if (intent?.getBooleanExtra(DevzFirebaseMessagingService.EXTRA_REQUEST_PERMISSION, false) == true) {
            requestNotificationPermission()
        }

        enableEdgeToEdge()
        setContent {
            // Read persisted appearance settings before the first themed frame so
            // there is no theme flash. Until loaded we show a neutral brand surface.
            val themeModeFlow = remember {
                userPreferencesRepository.observeThemeMode().map { ThemeMode.fromStorage(it) }
            }
            val languageFlow = remember {
                userPreferencesRepository.observeLanguage().map { AppLanguage.fromStorage(it) }
            }
            val themeMode by themeModeFlow.collectAsState(initial = null)
            val language by languageFlow.collectAsState(initial = null)

            // Apply the locale only when it differs from the currently active one,
            // otherwise AppCompat would recreate the activity in a loop.
            LaunchedEffect(language) {
                language?.let { applyLocaleIfNeeded(it) }
            }

            val resolvedTheme = themeMode
            if (resolvedTheme == null) {
                Box(modifier = Modifier.fillMaxSize().background(QBg))
                return@setContent
            }

            DevzTheme(themeMode = resolvedTheme) {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    DevzNavHost(
                        pendingQuestionId = pendingQuestionId,
                        pendingActorId = pendingActorId,
                        pendingJobId = pendingJobId,
                        pendingReportId = pendingReportId,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * Syncs the app locale with [language]. No-op when the desired locale is
     * already active (prevents AppCompat's recreate loop).
     */
    private fun applyLocaleIfNeeded(language: AppLanguage) {
        val desired = language.localeTag
            ?.let { LocaleListCompat.forLanguageTags(it) }
            ?: LocaleListCompat.getEmptyLocaleList()
        val active = AppCompatDelegate.getApplicationLocales()
        if (desired.toLanguageTags() != active.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(desired)
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
        val actorId = intent?.getIntExtra("actorId", 0)?.takeIf { it > 0 }

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            questionId?.let { putExtra("questionId", it) }
            actorId?.let { putExtra("actorId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, DevzFirebaseMessagingService.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
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
