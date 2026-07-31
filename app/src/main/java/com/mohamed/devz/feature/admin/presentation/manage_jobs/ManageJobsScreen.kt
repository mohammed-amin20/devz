package com.mohamed.devz.feature.admin.presentation.manage_jobs

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageJobsScreen(
    onNavigateUp: () -> Unit,
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

    if (uiState.showConfirmDialog && uiState.targetJob != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageJobsAction.DismissDialog) },
            containerColor = DevzCard,
            title = { Text("Approve Job", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Approve \"${uiState.targetJob!!.title}\" by ${uiState.targetJob!!.companyName}?",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmApprove() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                ) {
                    Text("Approve", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageJobsAction.DismissDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    Scaffold(
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = { Text("Manage Jobs", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite, modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            }
            uiState.jobs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No job postings found", color = QOnSurfaceVariant, fontSize = 16.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.jobs, key = { it.id }) { job ->
                        JobCard(
                            job = job,
                            logoUrl = uiState.companyLogos[job.accountId] ?: "",
                            onApprove = { viewModel.onAction(ManageJobsAction.ApproveJob(job)) },
                            onReject = { viewModel.onAction(ManageJobsAction.RejectJob(job)) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun JobCard(
    job: com.mohamed.devz.feature.core.domain.model.JobPosting,
    logoUrl: String,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = job.title,
                    color = TextWhite,
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
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${job.companyName} \u2022 ${job.jobType.replace("-", " ").replaceFirstChar { it.uppercase() }}",
                    color = TextGray,
                    fontSize = 13.sp,
                )
            }

            if (job.salaryRange.isNotBlank()) {
                Text(
                    text = job.salaryRange,
                    color = CyanPrimary,
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    ) {
                        Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    ) {
                        Text("Approve", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (color, label) = when (status) {
        "approved" -> Color(0xFF4CAF50) to "Approved"
        "pending" -> Color(0xFFFFC107) to "Pending"
        "rejected" -> Color(0xFFFF5252) to "Rejected"
        else -> TextGray to status
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
