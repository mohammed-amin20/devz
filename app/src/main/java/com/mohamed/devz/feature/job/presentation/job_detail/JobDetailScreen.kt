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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
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

    LaunchedEffect(jobId) {
        viewModel.onAction(JobDetailAction.LoadJob(jobId))
    }

    LaunchedEffect(uiState.applicationSuccess) {
        if (uiState.applicationSuccess) {
            snackbarHostState.showSnackbar("Application submitted!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = QBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QBg)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                    )
                }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            }

            uiState.error != null && uiState.job == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.error!!.asString(),
                        color = QOutline,
                    )
                }
            }

            uiState.job != null -> {
                val job = uiState.job!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CyanPrimary.copy(alpha = 0.1f),
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
                                    tint = CyanPrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CyanPrimary.copy(alpha = 0.1f))
                                        .padding(12.dp)
                                        .clickable { onProfileClick(job.accountId) },
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = job.companyName,
                                color = CyanPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onProfileClick(job.accountId) },
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = job.title,
                                color = QOnSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                if (job.salaryRange.isNotBlank()) {
                                    Text(
                                        text = job.salaryRange,
                                        color = QOnSurfaceVariant,
                                        fontSize = 14.sp,
                                    )
                                }
                                Text(
                                    text = job.jobType.replace("-", " ").replaceFirstChar { it.uppercase() },
                                    color = QOnSurfaceVariant,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Description",
                        color = QOnSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = job.description,
                        color = QOnSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Posted ${formatTimestamp(job.createdAt)}",
                        color = TextGray,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${job.applicantCount} applicant(s)",
                            color = QOnSurfaceVariant,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        val statusColor = if (job.status == "filled") Color(0xFFFFA726) else Color(0xFF4CAF50)
                        val statusLabel = if (job.status == "filled") "Reserved" else "Offered"
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

                    Button(
                        onClick = { viewModel.showApplySheet() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = job.status != "filled" && !uiState.hasApplied,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color(0xFF00363E),
                            disabledContainerColor = TextGray.copy(alpha = 0.3f),
                            disabledContentColor = TextGray,
                        ),
                    ) {
                        Text(
                            text = when {
                                job.status == "filled" -> "Reserved"
                                uiState.hasApplied -> "Applied"
                                else -> "\u2709\uFE0F Apply"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (uiState.showApplySheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { viewModel.hideApplySheet() },
                sheetState = sheetState,
                containerColor = DevzCard,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Apply for ${uiState.job?.title ?: ""}",
                        color = QOnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onAction(JobDetailAction.EmailChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Email address", color = TextGray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = QSurfaceHigh,
                            unfocusedContainerColor = QSurfaceHigh,
                            focusedIndicatorColor = CyanPrimary,
                            unfocusedIndicatorColor = QOutline,
                            cursorColor = CyanPrimary,
                            focusedTextColor = QOnSurface,
                            unfocusedTextColor = QOnSurface,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = uiState.whatsapp,
                        onValueChange = { viewModel.onAction(JobDetailAction.WhatsAppChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("WhatsApp number", color = TextGray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = QSurfaceHigh,
                            unfocusedContainerColor = QSurfaceHigh,
                            focusedIndicatorColor = CyanPrimary,
                            unfocusedIndicatorColor = QOutline,
                            cursorColor = CyanPrimary,
                            focusedTextColor = QOnSurface,
                            unfocusedTextColor = QOnSurface,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = uiState.coverLetter,
                        onValueChange = { viewModel.onAction(JobDetailAction.CoverLetterChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = {
                            Text(
                                "Write your proposal...",
                                color = TextGray,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = QSurfaceHigh,
                            unfocusedContainerColor = QSurfaceHigh,
                            focusedIndicatorColor = CyanPrimary,
                            unfocusedIndicatorColor = QOutline,
                            cursorColor = CyanPrimary,
                            focusedTextColor = QOnSurface,
                            unfocusedTextColor = QOnSurface,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.onAction(JobDetailAction.SubmitApplication(onSuccess = {})) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = uiState.coverLetter.isNotBlank() && !uiState.isSubmitting,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color(0xFF00363E),
                        ),
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color(0xFF00363E),
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Save",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
