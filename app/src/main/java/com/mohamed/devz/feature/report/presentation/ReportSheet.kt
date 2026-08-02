package com.mohamed.devz.feature.report.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.TextGray
import kotlinx.coroutines.delay

private val REPORT_REASONS = listOf("Spam", "Harassment", "Inappropriate", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSheet(
    target: ReportTarget,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(target) {
        viewModel.onAction(ReportAction.Reset)
    }

    LaunchedEffect(uiState.submitted) {
        if (uiState.submitted) {
            delay(1500)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DevzCard,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            if (uiState.submitted) {
                Spacer(modifier = Modifier.height(24.dp))
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = QPrimary,
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Report submitted",
                    color = QOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Our team will review it. Thank you.",
                    color = QOnSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                val label = target.reportedType.replaceFirstChar { it.uppercase() }
                Text(
                    text = "Report this $label",
                    color = QOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = QSurfaceHigh,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = target.preview,
                        color = QOnSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(14.dp),
                        maxLines = 3,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "WHY ARE YOU REPORTING THIS?",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReasonChip(
                        label = REPORT_REASONS[0],
                        selected = uiState.reason == REPORT_REASONS[0],
                        onClick = { viewModel.onAction(ReportAction.ReasonSelected(REPORT_REASONS[0])) },
                        modifier = Modifier.weight(1f),
                    )
                    ReasonChip(
                        label = REPORT_REASONS[1],
                        selected = uiState.reason == REPORT_REASONS[1],
                        onClick = { viewModel.onAction(ReportAction.ReasonSelected(REPORT_REASONS[1])) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReasonChip(
                        label = REPORT_REASONS[2],
                        selected = uiState.reason == REPORT_REASONS[2],
                        onClick = { viewModel.onAction(ReportAction.ReasonSelected(REPORT_REASONS[2])) },
                        modifier = Modifier.weight(1f),
                    )
                    ReasonChip(
                        label = REPORT_REASONS[3],
                        selected = uiState.reason == REPORT_REASONS[3],
                        onClick = { viewModel.onAction(ReportAction.ReasonSelected(REPORT_REASONS[3])) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = uiState.details,
                    onValueChange = { viewModel.onAction(ReportAction.DetailsChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Add details (optional)", color = TextGray) },
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

                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error.asString(),
                        color = QError,
                        fontSize = 13.sp,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.onAction(ReportAction.Submit(target)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState.reason != null && !uiState.isSubmitting,
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
                            text = "Submit report",
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

@Composable
private fun ReasonChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) CyanPrimary.copy(alpha = 0.15f) else QSurfaceHigh)
            .border(
                width = 1.dp,
                color = if (selected) CyanPrimary else QOutline,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) CyanPrimary else QOnSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
