package com.mohamed.devz.feature.admin.presentation.manage_reports

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.QPrimary
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

private val FILTERS = listOf("ALL", "PENDING", "REVIEWED", "DISMISSED")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageReportsScreen(
    onNavigateUp: () -> Unit,
    viewModel: ManageReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.lastActionMessage) {
        uiState.lastActionMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        containerColor = QBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Reports",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FILTERS.forEachIndexed { index, label ->
                    FilterChip(
                        selected = uiState.selectedFilter == index,
                        onClick = { viewModel.onAction(ManageReportsAction.FilterSelected(index)) },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DevzCard,
                            selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                            labelColor = QOnSurfaceVariant,
                            selectedLabelColor = CyanPrimary,
                            selectedLeadingIconColor = CyanPrimary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedFilter == index,
                            borderColor = QOnSurfaceVariant.copy(alpha = 0.3f),
                            selectedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                            borderWidth = 1.dp,
                        ),
                    )
                }
            }

            when {
                uiState.isLoading && uiState.allReports.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }

                uiState.error != null && uiState.allReports.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Report,
                            contentDescription = null,
                            tint = QError,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error!!.asString(),
                            color = QOnSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onAction(ManageReportsAction.Refresh) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color(0xFF00363E)
                            )
                        ) {
                            Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.onAction(ManageReportsAction.Refresh) },
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize(),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = uiState.isRefreshing,
                                state = pullRefreshState,
                                color = CyanPrimary,
                            )
                        },
                    ) {
                        if (uiState.filteredReports.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reports here",
                                    color = TextGray,
                                    fontSize = 14.sp,
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 4.dp,
                                    bottom = 24.dp,
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(uiState.filteredReports, key = { it.report.id }) { model ->
                                    ReportCard(
                                        model = model,
                                        onClick = { viewModel.onAction(ManageReportsAction.SelectReport(model.report)) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.selectedReport?.let { selected ->
        ReportDetailSheet(
            model = selected,
            isProcessing = uiState.isProcessing,
            onDismiss = { viewModel.onAction(ManageReportsAction.DismissDetail) },
            onDismissReport = { viewModel.onAction(ManageReportsAction.DismissReport(selected.report)) },
            onDeleteContent = { viewModel.onAction(ManageReportsAction.DeleteContent(selected.report)) },
            onBanUser = { viewModel.onAction(ManageReportsAction.BanUser(selected.report)) },
        )
    }
}

@Composable
private fun ReportCard(
    model: ReportUiModel,
    onClick: () -> Unit,
) {
    val report = model.report
    val statusColor = when (report.status) {
        "pending" -> Color(0xFFFFA726)
        "reviewed" -> QPrimary
        "dismissed" -> TextGray
        else -> TextGray
    }
    val typeLabel = report.reportedType.replaceFirstChar { it.uppercase() }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = DevzCard,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = 0.15f),
                ) {
                    Text(
                        text = typeLabel,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = report.status.uppercase(),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Reason: ${report.reason}",
                color = QOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
            )

            if (report.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.details,
                    color = QOnSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (model.targetTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Target: ${model.targetTitle}",
                    color = TextGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (model.reporterAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = model.reporterAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(QSurfaceHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reported by ${model.reporterName}",
                    color = TextGray,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTimestamp(report.createdAt),
                    color = TextGray,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportDetailSheet(
    model: ReportUiModel,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onDismissReport: () -> Unit,
    onDeleteContent: () -> Unit,
    onBanUser: () -> Unit,
) {
    val report = model.report
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isContentReport = report.reportedType in setOf("question", "answer", "job")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DevzCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
        ) {
            Text(
                text = "Report Details",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(label = "Reported type", value = report.reportedType.replaceFirstChar { it.uppercase() })
            DetailRow(label = "Reason", value = report.reason)
            if (report.details.isNotBlank()) {
                DetailRow(label = "Details", value = report.details)
            }
            if (model.targetTitle.isNotBlank()) {
                DetailRow(label = "Target", value = model.targetTitle)
            }
            DetailRow(label = "Reporter", value = model.reporterName)
            DetailRow(label = "Reported at", value = formatTimestamp(report.createdAt))
            DetailRow(label = "Status", value = report.status.uppercase())

            Spacer(modifier = Modifier.height(24.dp))

            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CyanPrimary, strokeWidth = 2.dp)
                }
            } else {
                if (isContentReport) {
                    Button(
                        onClick = onDeleteContent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QError,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Delete content", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = onBanUser,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = QError,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QError.copy(alpha = 0.6f)),
                ) {
                    Text("Ban user", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismissReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = QOnSurfaceVariant,
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QOnSurfaceVariant.copy(alpha = 0.4f)),
                ) {
                    Text("Dismiss report", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = "$label:",
            color = TextGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp),
        )
        Text(
            text = value,
            color = QOnSurface,
            fontSize = 13.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}
