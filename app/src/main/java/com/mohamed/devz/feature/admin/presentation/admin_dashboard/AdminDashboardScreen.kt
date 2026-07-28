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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.admin.presentation.admin_dashboard.components.StatCard
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToAnswers: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToJobs: () -> Unit = {},
    onNavigateToCompanies: () -> Unit = {},
    onNavigateUp: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Panel",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = CyanPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "USERS",
                        value = "${uiState.totalUsers}",
                        emoji = "\uD83D\uDC65",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "QUESTIONS",
                        value = "${uiState.totalQuestions}",
                        emoji = "❓",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "ANSWERS",
                        value = "${uiState.totalAnswers}",
                        emoji = "\uD83D\uDCAC",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "BANNED",
                        value = "${uiState.bannedUsers}",
                        emoji = "\u26D4\uFE0F",
                        valueColor = QError,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AdminMenuItem(
                emoji = "\uD83D\uDC65",
                label = "Users",
                onClick = onNavigateToUsers
            )
            AdminMenuItem(
                emoji = "❓",
                label = "Questions",
                onClick = onNavigateToQuestions
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCAC",
                label = "Answers",
                onClick = onNavigateToAnswers
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCE2",
                label = "Announcements",
                onClick = onNavigateToAnnouncements
            )
            AdminMenuItem(
                emoji = "\uD83D\uDCBC",
                label = "Jobs",
                onClick = onNavigateToJobs
            )
            AdminMenuItem(
                emoji = "\uD83C\uDFED",
                label = "Companies",
                onClick = onNavigateToCompanies
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
        color = DevzCard,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = QOnSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
