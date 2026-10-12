package com.mohamed.devz.feature.admin.presentation.manage_reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

private val FILTERS = listOf(
    R.string.admin_filter_all,
    R.string.admin_status_pending,
    R.string.admin_status_reviewed,
    R.string.admin_status_dismissed,
)

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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_reports_title),
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FILTERS.forEachIndexed { index, labelRes ->
                    FilterChip(
                        selected = uiState.selectedFilter == index,
                        onClick = { viewModel.onAction(ManageReportsAction.FilterSelected(index)) },
                        label = { Text(stringResource(labelRes), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedFilter == index,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            borderWidth = 1.dp,
                        ),
                    )
                }
            }

            when {
                uiState.isLoading && uiState.allReports.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.error != null && uiState.allReports.isEmpty() -> {
                    ErrorState(
                        message = uiState.error!!.asString(),
                        onRetry = { viewModel.onAction(ManageReportsAction.Refresh) },
                        retryLabel = stringResource(R.string.action_retry),
                    )
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
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                    ) {
                        if (uiState.filteredReports.isEmpty()) {
                            EmptyState(
                                icon = Icons.Filled.Report,
                                title = stringResource(R.string.admin_empty_reports),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
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
private fun typeLabel(type: String): String {
    val resId = when (type) {
        "question" -> R.string.report_type_question
        "answer" -> R.string.report_type_answer
        "job" -> R.string.report_type_job
        "user" -> R.string.report_type_user
        else -> null
    }
    val value = if (resId != null) stringResource(resId) else type
    return value.replaceFirstChar { it.titlecase() }
}

@Composable
private fun reasonLabel(reason: String): String {
    val resId = when (reason.trim()) {
        "Spam" -> R.string.report_reason_spam
        "Harassment" -> R.string.report_reason_harassment
        "Inappropriate" -> R.string.report_reason_inappropriate
        "Other" -> R.string.report_reason_other
        else -> null
    }
    return if (resId != null) stringResource(resId) else reason
}

@Composable
private fun statusLabel(status: String): String {
    val resId = when (status) {
        "pending" -> R.string.admin_status_pending
        "reviewed" -> R.string.admin_status_reviewed
        "dismissed" -> R.string.admin_status_dismissed
        else -> null
    }
    return if (resId != null) stringResource(resId) else status
}

@Composable
private fun statusColor(status: String) = when (status) {
    "pending" -> MaterialTheme.extendedColors.warning
    "reviewed" -> MaterialTheme.colorScheme.primary
    "dismissed" -> MaterialTheme.colorScheme.onSurfaceVariant
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun ReportCard(
    model: ReportUiModel,
    onClick: () -> Unit,
) {
    val report = model.report
    val accent = statusColor(report.status)

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.copy(alpha = 0.15f),
                ) {
                    Text(
                        text = typeLabel(report.reportedType),
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = statusLabel(report.status),
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.admin_report_reason_label, reasonLabel(report.reason)),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
            )

            if (report.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.details,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (model.targetTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.admin_report_target_label, model.targetTitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.admin_report_reported_by, model.reporterName),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTimestamp(report.createdAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
        ) {
            Text(
                text = stringResource(R.string.admin_report_details_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = stringResource(R.string.admin_report_field_type),
                value = typeLabel(report.reportedType),
            )
            DetailRow(
                label = stringResource(R.string.admin_report_field_reason),
                value = reasonLabel(report.reason),
            )
            if (report.details.isNotBlank()) {
                DetailRow(
                    label = stringResource(R.string.admin_report_field_details),
                    value = report.details,
                )
            }
            if (model.targetTitle.isNotBlank()) {
                DetailRow(
                    label = stringResource(R.string.admin_report_field_target),
                    value = model.targetTitle,
                )
            }
            DetailRow(
                label = stringResource(R.string.admin_report_field_reporter),
                value = model.reporterName,
            )
            DetailRow(
                label = stringResource(R.string.admin_report_field_reported_at),
                value = formatTimestamp(report.createdAt),
            )
            DetailRow(
                label = stringResource(R.string.admin_report_field_status),
                value = statusLabel(report.status),
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
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
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    ) {
                        Text(stringResource(R.string.admin_report_delete_content), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = onBanUser,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                ) {
                    Text(stringResource(R.string.admin_report_ban_user), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismissReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
                ) {
                    Text(stringResource(R.string.admin_report_dismiss), fontWeight = FontWeight.SemiBold)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp),
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageReportsDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageReportsScreen(onNavigateUp = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageReportsLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageReportsScreen(onNavigateUp = {})
    }
}
