package com.mohamed.devz.feature.job.presentation.job_detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.feature.job.presentation.job_detail.components.ApplySheet
import com.mohamed.devz.feature.report.presentation.ReportMenu
import com.mohamed.devz.feature.report.presentation.ReportSheet
import com.mohamed.devz.feature.report.presentation.ReportTarget
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Int,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JobDetailViewModel = hiltViewModel(),
    onProfileClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val submittedMessage = stringResource(R.string.jobdetail_submitted)

    LaunchedEffect(jobId) {
        viewModel.onAction(JobDetailAction.LoadJob(jobId))
    }

    LaunchedEffect(uiState.applicationSuccess) {
        if (uiState.applicationSuccess) {
            snackbarHostState.showSnackbar(submittedMessage)
        }
    }

    Box(modifier = modifier) {
        JobDetailContent(
            state = uiState,
            onNavigateUp = navigateUp,
            onProfileClick = onProfileClick,
            onRetry = { viewModel.onAction(JobDetailAction.LoadJob(jobId)) },
            onShowReport = { viewModel.onAction(JobDetailAction.ShowReport(it)) },
            onApply = { viewModel.showApplySheet() },
            modifier = Modifier.fillMaxSize(),
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (uiState.showApplySheet) {
        ApplySheet(
            state = uiState,
            onAction = viewModel::onAction,
            onDismiss = { viewModel.hideApplySheet() },
        )
    }

    uiState.reportTarget?.let { target ->
        ReportSheet(
            target = target,
            onDismiss = { viewModel.onAction(JobDetailAction.DismissReport) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailContent(
    state: JobDetailState,
    onNavigateUp: () -> Unit,
    onProfileClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onShowReport: (ReportTarget) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.jobdetail_back),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                val jobForReport = state.job
                if (jobForReport != null && state.currentAccountId != 0 && jobForReport.accountId != state.currentAccountId) {
                    ReportMenu(
                        onReport = {
                            onShowReport(
                                ReportTarget(
                                    reportedType = "job",
                                    reportedId = jobForReport.id,
                                    preview = jobForReport.title,
                                )
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            state.error != null && state.job == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    ErrorState(
                        message = state.error!!.asString(),
                        onRetry = onRetry,
                        retryLabel = stringResource(R.string.action_retry),
                    )
                }
            }

            state.job != null -> {
                val job = state.job!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    JobHeader(job = job, onProfileClick = onProfileClick)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.jobdetail_description),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = job.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(
                            R.string.jobdetail_posted,
                            formatTimestamp(job.createdAt)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.jobdetail_applicants_count, job.applicantCount),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        val isReserved = job.status == "filled"
                        val statusColor = if (isReserved) {
                            MaterialTheme.extendedColors.warning
                        } else {
                            MaterialTheme.extendedColors.success
                        }
                        val statusLabel = if (isReserved) {
                            stringResource(R.string.jobdetail_status_reserved)
                        } else {
                            stringResource(R.string.jobdetail_status_offered)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    DevzButton(
                        label = when {
                            job.status == "filled" -> stringResource(R.string.jobdetail_status_reserved)
                            state.hasApplied -> stringResource(R.string.jobdetail_applied)
                            else -> stringResource(R.string.jobdetail_apply)
                        },
                        onClick = onApply,
                        enabled = job.status != "filled" && !state.hasApplied,
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun JobHeader(
    job: JobDetailUiModel,
    onProfileClick: (Int) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (job.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = job.logoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onProfileClick(job.accountId) },
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    Icons.Filled.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(12.dp)
                        .clickable { onProfileClick(job.accountId) },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = job.companyName,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onProfileClick(job.accountId) },
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (job.salaryRange.isNotBlank()) {
                    Text(
                        text = job.salaryRange,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
                Text(
                    text = jobTypeLabel(job.jobType),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun jobTypeLabel(type: String): String = stringResource(
    when (type) {
        "full-time" -> R.string.jobtype_full_time
        "part-time" -> R.string.jobtype_part_time
        "remote" -> R.string.jobtype_remote
        "contract" -> R.string.jobtype_contract
        else -> R.string.jobtype_full_time
    }
)

@Preview
@Composable
private fun PreviewJobDetailDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        JobDetailContent(
            state = JobDetailState(
                job = JobDetailUiModel(
                    id = 1,
                    accountId = 1,
                    companyName = "Acme Labs",
                    title = "Senior Android Developer",
                    description = "Build great things.",
                    salaryRange = "$80k - $120k",
                    jobType = "full-time",
                    createdAt = "2024-01-01T00:00:00Z",
                    status = "approved",
                    applicantCount = 3,
                ),
                currentAccountId = 2,
            ),
            onNavigateUp = {},
            onProfileClick = {},
            onRetry = {},
            onShowReport = {},
            onApply = {},
        )
    }
}

@Preview
@Composable
private fun PreviewJobDetailLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        JobDetailContent(
            state = JobDetailState(
                job = JobDetailUiModel(
                    id = 1,
                    accountId = 1,
                    companyName = "Acme Labs",
                    title = "Senior Android Developer",
                    description = "Build great things.",
                    salaryRange = "$80k - $120k",
                    jobType = "full-time",
                    createdAt = "2024-01-01T00:00:00Z",
                    status = "approved",
                    applicantCount = 3,
                ),
                currentAccountId = 2,
            ),
            onNavigateUp = {},
            onProfileClick = {},
            onRetry = {},
            onShowReport = {},
            onApply = {},
        )
    }
}