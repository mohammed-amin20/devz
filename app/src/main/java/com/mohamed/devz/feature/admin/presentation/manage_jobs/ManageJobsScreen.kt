package com.mohamed.devz.feature.admin.presentation.manage_jobs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageJobsScreen(
    onNavigateUp: () -> Unit,
    onCompanyClick: (Int) -> Unit = {},
    onJobClick: (Int) -> Unit = {},
    viewModel: ManageJobsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (error is UiText.DynamicString) {
                snackbarHostState.showSnackbar(error.value)
            }
        }
    }

    ManageJobsContent(
        state = uiState,
        onNavigateUp = onNavigateUp,
        onCompanyClick = onCompanyClick,
        onJobClick = onJobClick,
        onApproveJob = { viewModel.onAction(ManageJobsAction.ApproveJob(it)) },
        onRejectJob = { viewModel.onAction(ManageJobsAction.RejectJob(it)) },
        onConfirmApprove = { viewModel.confirmApprove() },
        onDismissDialog = { viewModel.onAction(ManageJobsAction.DismissDialog) },
        onRefresh = { viewModel.onAction(ManageJobsAction.Refresh) },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageJobsContent(
    state: ManageJobsState,
    onNavigateUp: () -> Unit,
    onCompanyClick: (Int) -> Unit,
    onJobClick: (Int) -> Unit,
    onApproveJob: (JobPosting) -> Unit,
    onRejectJob: (JobPosting) -> Unit,
    onConfirmApprove: () -> Unit,
    onDismissDialog: () -> Unit,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    if (state.showConfirmDialog && state.targetJob != null) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_approve_job_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.admin_approve_job_message,
                        state.targetJob!!.title,
                        state.targetJob!!.companyName,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.success,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(stringResource(R.string.action_approve))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_jobs_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            state.jobs.isEmpty() -> {
                EmptyState(
                    icon = Icons.Filled.Work,
                    title = stringResource(R.string.admin_empty_jobs),
                    modifier = Modifier.padding(innerPadding),
                )
            }
            else -> {
                val pullRefreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    state = pullRefreshState,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = state.isRefreshing,
                            state = pullRefreshState,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.jobs, key = { it.id }) { job ->
                            JobCard(
                                job = job,
                                logoUrl = state.companyLogos[job.accountId] ?: "",
                                onApprove = { onApproveJob(job) },
                                onReject = { onRejectJob(job) },
                                onCompanyClick = { onCompanyClick(job.accountId) },
                                onClick = { onJobClick(job.id) },
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun jobTypeLabel(jobType: String): String {
    val resId = when (jobType) {
        "full-time" -> R.string.jobtype_full_time
        "part-time" -> R.string.jobtype_part_time
        "remote" -> R.string.jobtype_remote
        "contract" -> R.string.jobtype_contract
        else -> null
    }
    return if (resId != null) stringResource(resId) else jobType
}

@Composable
private fun JobCard(
    job: JobPosting,
    logoUrl: String,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCompanyClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = job.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(job.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onCompanyClick),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onCompanyClick),
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${job.companyName} \u2022 ${jobTypeLabel(job.jobType)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable(onClick = onCompanyClick),
                )
            }

            if (job.salaryRange.isNotBlank()) {
                Text(
                    text = job.salaryRange,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (job.status == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Text(stringResource(R.string.action_reject), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.extendedColors.success,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(stringResource(R.string.action_approve), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status) {
        "approved" -> MaterialTheme.extendedColors.success
        "pending" -> MaterialTheme.extendedColors.warning
        "rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val label = when (status) {
        "approved" -> stringResource(R.string.admin_status_approved)
        "pending" -> stringResource(R.string.admin_status_pending)
        "rejected" -> stringResource(R.string.admin_status_rejected)
        else -> status
    }
    Text(
        text = label,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageJobsDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageJobsContent(
            state = ManageJobsState(
                jobs = listOf(previewJob),
                companyLogos = emptyMap(),
                isLoading = false,
            ),
            onNavigateUp = {},
            onCompanyClick = {},
            onJobClick = {},
            onApproveJob = {},
            onRejectJob = {},
            onConfirmApprove = {},
            onDismissDialog = {},
            onRefresh = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageJobsLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageJobsContent(
            state = ManageJobsState(
                jobs = listOf(previewJob),
                companyLogos = emptyMap(),
                isLoading = false,
            ),
            onNavigateUp = {},
            onCompanyClick = {},
            onJobClick = {},
            onApproveJob = {},
            onRejectJob = {},
            onConfirmApprove = {},
            onDismissDialog = {},
            onRefresh = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

private val previewJob = JobPosting(
    id = 1,
    companyName = "Tech Co",
    title = "Senior Android Developer",
    description = "We are looking for a senior Android developer.",
    salaryRange = "$5000",
    jobType = "full-time",
    status = "pending",
    createdAt = "2024-01-01T00:00:00Z",
    accountId = 42,
)