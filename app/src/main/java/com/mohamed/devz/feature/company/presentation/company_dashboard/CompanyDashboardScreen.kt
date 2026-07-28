package com.mohamed.devz.feature.company.presentation.company_dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

@Composable
fun CompanyDashboardScreen(
    onPostJob: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompanyDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QBg)
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
            IconButton(onClick = { viewModel.onAction(CompanyDashboardAction.Refresh) }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = TextGray)
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
                        Button(
                            onClick = onPostJob,
                            enabled = uiState.subscriptionStatus == "active",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color(0xFF00363E),
                                disabledContainerColor = QSurfaceHigh,
                                disabledContentColor = TextGray,
                            ),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Post New Job", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Text(
                            text = "My Job Postings",
                            color = QOnSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    if (uiState.jobPostings.isEmpty()) {
                        item {
                            Text(
                                text = "No job postings yet",
                                color = QOnSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    } else {
                        items(uiState.jobPostings, key = { it.id }) { job ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DevzCard,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
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
                                    val badgeColor = when (job.status) {
                                        "approved" -> Color(0xFF4CAF50)
                                        "pending" -> Color(0xFFFFC107)
                                        "rejected" -> Color(0xFFFF5252)
                                        else -> TextGray
                                    }
                                    Text(
                                        text = job.status.replaceFirstChar { it.uppercase() },
                                        color = badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
