package com.mohamed.devz.feature.admin.presentation.admin_dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.admin.presentation.admin_dashboard.components.StatCard
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToAnswers: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToJobs: () -> Unit = {},
    onNavigateToCompanies: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateUp: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AdminDashboardContent(
        state = uiState,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToQuestions = onNavigateToQuestions,
        onNavigateToAnswers = onNavigateToAnswers,
        onNavigateToAnnouncements = onNavigateToAnnouncements,
        onNavigateToJobs = onNavigateToJobs,
        onNavigateToCompanies = onNavigateToCompanies,
        onNavigateToReports = onNavigateToReports,
        onNavigateUp = onNavigateUp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    state: AdminDashboardState,
    onNavigateToUsers: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToAnswers: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToJobs: () -> Unit = {},
    onNavigateToCompanies: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_admin_panel),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = stringResource(R.string.admin_stat_users),
                        value = "${state.totalUsers}",
                        emoji = "\uD83D\uDC65",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = stringResource(R.string.admin_stat_questions),
                        value = "${state.totalQuestions}",
                        emoji = "❓",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        label = stringResource(R.string.admin_stat_answers),
                        value = "${state.totalAnswers}",
                        emoji = "\uD83D\uDCAC",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = stringResource(R.string.admin_stat_banned),
                        value = "${state.bannedUsers}",
                        emoji = "\u26D4\uFE0F",
                        valueColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AdminMenuItem(
                emoji = "\uD83D\uDC65",
                label = stringResource(R.string.admin_menu_users),
                onClick = onNavigateToUsers,
            )
            AdminMenuItem(
                emoji = "❓",
                label = stringResource(R.string.admin_menu_questions),
                onClick = onNavigateToQuestions,
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCAC",
                label = stringResource(R.string.admin_menu_answers),
                onClick = onNavigateToAnswers,
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCE2",
                label = stringResource(R.string.admin_menu_announcements),
                onClick = onNavigateToAnnouncements,
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCBC",
                label = stringResource(R.string.admin_menu_jobs),
                onClick = onNavigateToJobs,
            )
            AdminMenuItem(
                emoji = "\uD83C\uDFED",
                label = stringResource(R.string.admin_menu_companies),
                onClick = onNavigateToCompanies,
            )
            AdminMenuItem(
                emoji = "\uD83D\uDEA9",
                label = stringResource(R.string.admin_menu_reports),
                onClick = onNavigateToReports,
            )
        }
    }
}

@Composable
private fun AdminMenuItem(
    emoji: String,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewAdminDashboardDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        AdminDashboardContent(
            state = AdminDashboardState(
                totalUsers = 120,
                totalQuestions = 340,
                totalAnswers = 890,
                bannedUsers = 3,
                isLoading = false,
            ),
            onNavigateToUsers = {},
            onNavigateToQuestions = {},
            onNavigateToAnswers = {},
            onNavigateToAnnouncements = {},
            onNavigateUp = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewAdminDashboardLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        AdminDashboardContent(
            state = AdminDashboardState(
                totalUsers = 120,
                totalQuestions = 340,
                totalAnswers = 890,
                bannedUsers = 3,
                isLoading = false,
            ),
            onNavigateToUsers = {},
            onNavigateToQuestions = {},
            onNavigateToAnswers = {},
            onNavigateToAnnouncements = {},
            onNavigateUp = {},
        )
    }
}