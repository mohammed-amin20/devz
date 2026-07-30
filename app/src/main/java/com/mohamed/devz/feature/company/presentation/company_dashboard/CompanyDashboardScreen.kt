package com.mohamed.devz.feature.company.presentation.company_dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp

@Composable
fun CompanyDashboardScreen(
    onPostJob: () -> Unit,
    onJobClick: (Int) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CompanyDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(QBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Company",
                    color = QOnSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
                Row {
                    IconButton(onClick = { viewModel.onAction(CompanyDashboardAction.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = TextGray)
                    }
                    IconButton(
                        onClick = {
                            viewModel.onAction(CompanyDashboardAction.Logout)
                            onLogout()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = TextGray,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = uiState.error!!.asString(), color = QOutline, fontSize = 14.sp)
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DevzCard,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(
                                        Icons.Filled.Business,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(CyanPrimary.copy(alpha = 0.1f))
                                            .padding(12.dp)
                                            .size(32.dp),
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = uiState.companyName,
                                        color = TextWhite,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if (uiState.website.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Language,
                                                contentDescription = null,
                                                tint = TextGray,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = uiState.website,
                                                color = CyanPrimary,
                                                fontSize = 13.sp,
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val statusColor = when (uiState.subscriptionStatus) {
                                        "active" -> Color(0xFF4CAF50)
                                        "pending" -> Color(0xFFFFC107)
                                        else -> QOutline
                                    }
                                    val statusText = when (uiState.subscriptionStatus) {
                                        "active" -> "Active"
                                        "pending" -> "Pending Approval"
                                        "expired" -> "Expired"
                                        else -> uiState.subscriptionStatus
                                    }
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            PrimaryTabRow(
                                selectedTabIndex = uiState.selectedTab,
                                containerColor = QBg,
                            ) {
                                Tab(
                                    selected = uiState.selectedTab == 0,
                                    onClick = { viewModel.onAction(CompanyDashboardAction.SelectTab(0)) },
                                    text = {
                                        Text(
                                            "Offered (${uiState.offeredJobs.size})",
                                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 0) CyanPrimary else TextGray,
                                        )
                                    },
                                )
                                Tab(
                                    selected = uiState.selectedTab == 1,
                                    onClick = { viewModel.onAction(CompanyDashboardAction.SelectTab(1)) },
                                    text = {
                                        Text(
                                            "Reserved (${uiState.reservedJobs.size})",
                                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 1) CyanPrimary else TextGray,
                                        )
                                    },
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val currentJobs = if (uiState.selectedTab == 0) uiState.offeredJobs else uiState.reservedJobs

                        if (currentJobs.isEmpty()) {
                            item {
                                Text(
                                    text = if (uiState.selectedTab == 0) "No offered jobs" else "No reserved jobs",
                                    color = QOnSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                        } else {
                            items(currentJobs, key = { it.id }) { job ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DevzCard,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onJobClick(job.id) },
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = job.title,
                                                color = TextWhite,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = job.status.replaceFirstChar { it.uppercase() },
                                                color = Color(0xFF4CAF50),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                            )
                                        }
                                        if (job.createdAt.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = formatTimestamp(job.createdAt),
                                                color = TextGray,
                                                fontSize = 11.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        if (uiState.subscriptionStatus == "active" && !uiState.isLoading) {
            FloatingActionButton(
                onClick = onPostJob,
                containerColor = CyanPrimary,
                contentColor = Color(0xFF00363E),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Post New Job")
            }
        }
    }
}
