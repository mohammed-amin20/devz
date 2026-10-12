package com.mohamed.devz.feature.company.presentation.company_job_detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.presentation.components.ProBadge
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

private val WhatsAppGreen = Color(0xFF25D366)

@Composable
fun CompanyJobDetailScreen(
    jobId: Int,
    navigateUp: () -> Unit,
    onProfileClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CompanyJobDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.onAction(CompanyJobDetailAction.LoadJob(jobId))
    }

    CompanyJobDetailContent(
        state = uiState,
        onNavigateUp = navigateUp,
        onProfileClick = onProfileClick,
        onRetry = { viewModel.onAction(CompanyJobDetailAction.LoadJob(jobId)) },
        onApprove = { applicationId, applicantId ->
            viewModel.onAction(
                CompanyJobDetailAction.ApproveApplication(applicationId, applicantId)
            )
        },
        modifier = modifier,
    )
}

@Composable
fun CompanyJobDetailContent(
    state: CompanyJobDetailState,
    onNavigateUp: () -> Unit,
    onProfileClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onApprove: (applicationId: Int, applicantId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.jobdetail_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = stringResource(R.string.company_job_detail_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            state.error != null && state.job == null -> {
                ErrorState(
                    message = state.error!!.asString(),
                    onRetry = onRetry,
                    retryLabel = stringResource(R.string.action_retry),
                )
            }

            state.job != null -> {
                val job = state.job!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = job.title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    if (job.salaryRange.isNotBlank()) {
                                        Text(
                                            text = job.salaryRange,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    Text(
                                        text = jobTypeLabel(job.jobType),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = job.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.company_job_detail_applicants, state.proposals.size),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    if (state.proposals.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.company_job_detail_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    } else {
                        items(state.proposals, key = { it.applicationId }) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onProfileClick = { onProfileClick(proposal.applicantId) },
                                onApprove = {
                                    onApprove(proposal.applicationId, proposal.applicantId)
                                },
                                isApproving = state.isApproving,
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: ProposalUiModel,
    onProfileClick: () -> Unit,
    onApprove: () -> Unit,
    isApproving: Boolean,
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (proposal.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = proposal.avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onProfileClick),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = proposal.fullName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = proposal.fullName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable(onClick = onProfileClick),
                        )
                        if (proposal.isPro) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ProBadge()
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.pro,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.company_job_detail_pts, proposal.points),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            if (proposal.coverLetter.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = proposal.coverLetter,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (proposal.createdAt.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.company_job_detail_applied,
                        formatTimestamp(proposal.createdAt)
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:${proposal.email}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.company_job_detail_email), fontSize = 12.sp)
                }

                if (proposal.whatsapp.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${proposal.whatsapp}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.company_job_detail_whatsapp), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (proposal.status == "reserved") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.success,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.company_job_detail_approved),
                            color = MaterialTheme.extendedColors.success,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    Button(
                        onClick = onApprove,
                        enabled = !isApproving,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        if (isApproving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                stringResource(R.string.company_job_detail_approve),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
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

private val previewProposal = ProposalUiModel(
    applicationId = 1,
    applicantId = 2,
    fullName = "Sara Ali",
    username = "sara",
    avatarUrl = "",
    email = "sara@example.com",
    whatsapp = "+12345678",
    points = 120,
    status = "pending",
    coverLetter = "I am very interested in this role and have five years of Android experience.",
    createdAt = "2024-01-01T00:00:00Z",
    isPro = true,
)

@Preview
@Composable
private fun PreviewCompanyJobDetailDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        CompanyJobDetailContent(
            state = CompanyJobDetailState(
                job = JobPosting(
                    id = 1,
                    companyName = "Acme Labs",
                    title = "Senior Android Developer",
                    description = "Build great things.",
                    salaryRange = "$80k - $120k",
                    jobType = "full-time",
                ),
                proposals = listOf(previewProposal),
                isLoading = false,
            ),
            onNavigateUp = {},
            onProfileClick = {},
            onRetry = {},
            onApprove = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun PreviewCompanyJobDetailLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        CompanyJobDetailContent(
            state = CompanyJobDetailState(
                job = JobPosting(
                    id = 1,
                    companyName = "Acme Labs",
                    title = "Senior Android Developer",
                    description = "Build great things.",
                    salaryRange = "$80k - $120k",
                    jobType = "full-time",
                ),
                proposals = listOf(previewProposal),
                isLoading = false,
            ),
            onNavigateUp = {},
            onProfileClick = {},
            onRetry = {},
            onApprove = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun PreviewProposalCardDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ProposalCard(
            proposal = previewProposal,
            onProfileClick = {},
            onApprove = {},
            isApproving = false,
        )
    }
}

@Preview
@Composable
private fun PreviewProposalCardLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ProposalCard(
            proposal = previewProposal,
            onProfileClick = {},
            onApprove = {},
            isApproving = false,
        )
    }
}