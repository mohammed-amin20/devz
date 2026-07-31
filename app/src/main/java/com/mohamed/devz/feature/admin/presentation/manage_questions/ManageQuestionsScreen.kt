package com.mohamed.devz.feature.admin.presentation.manage_questions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageQuestionsScreen(
    onNavigateUp: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    viewModel: ManageQuestionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showHideDialog && uiState.targetHideQuestion != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageQuestionsAction.DismissHideDialog) },
            containerColor = DevzCard,
            title = {
                Text("Hide Question", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to hide \"${uiState.targetHideQuestion!!.title}\"? It will no longer be visible to users.",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageQuestionsAction.ConfirmHide(uiState.targetHideQuestion!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB4AB))
                ) {
                    Text("Hide", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageQuestionsAction.DismissHideDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    Scaffold(
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Manage Questions", color = TextWhite, fontWeight = FontWeight.Bold)
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onAction(ManageQuestionsAction.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search questions...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextGray)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = Color(0xFF2A3A3A),
                    focusedContainerColor = DevzCard,
                    unfocusedContainerColor = DevzCard,
                    cursorColor = CyanPrimary,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                ),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == 0,
                    onClick = { viewModel.onAction(ManageQuestionsAction.FilterAll) },
                    label = { Text("All", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = CyanPrimary,
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 0,
                    ),
                )
                FilterChip(
                    selected = uiState.selectedFilter == 1,
                    onClick = { viewModel.onAction(ManageQuestionsAction.FilterHidden) },
                    label = { Text("Hidden", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFB4AB).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFFFB4AB),
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = Color(0xFFFFB4AB).copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 1,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
                uiState.filteredQuestions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No questions found", color = QOnSurfaceVariant, fontSize = 16.sp)
                    }
                }
                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.onAction(ManageQuestionsAction.Refresh) },
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
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(uiState.filteredQuestions, key = { it.id }) { question ->
                                QuestionCard(
                                    question = question,
                                    authorName = uiState.authorNames[question.accountId] ?: "Unknown",
                                    onQuestionClick = { onQuestionClick(question.id) },
                                    onHide = { viewModel.onAction(ManageQuestionsAction.HideQuestion(question)) },
                                    onUnhide = { viewModel.onAction(ManageQuestionsAction.UnhideQuestion(question)) },
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionCard(
    question: Question,
    authorName: String,
    onQuestionClick: () -> Unit,
    onHide: () -> Unit,
    onUnhide: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onQuestionClick() },
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = question.title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (question.isHidden) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HIDDEN",
                        color = Color(0xFFFFB4AB),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFFFB4AB).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "by $authorName",
                color = TextGray,
                fontSize = 13.sp,
            )

            if (question.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    question.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                        Text(
                            text = tag,
                            color = CyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(CyanPrimary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${question.answersCount} answers",
                        color = TextGray,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "${question.likesCount} likes",
                        color = TextGray,
                        fontSize = 12.sp,
                    )
                }

                if (question.isHidden) {
                    OutlinedButton(
                        onClick = onUnhide,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                    ) {
                        Text("Unhide", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onHide,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB4AB)),
                        border = BorderStroke(1.dp, Color(0xFFFFB4AB).copy(alpha = 0.5f)),
                    ) {
                        Text("Hide", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
