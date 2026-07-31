package com.mohamed.devz.feature.admin.presentation.manage_answers

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnswersScreen(
    onNavigateUp: () -> Unit,
    onAnswerClick: (Int) -> Unit,
    viewModel: ManageAnswersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDialog && uiState.targetDeleteAnswer != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageAnswersAction.DismissDeleteDialog) },
            containerColor = DevzCard,
            title = {
                Text("Delete Answer", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this answer? This action cannot be undone.",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageAnswersAction.ConfirmDelete(uiState.targetDeleteAnswer!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = QError)
                ) {
                    Text("Delete", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageAnswersAction.DismissDeleteDialog) }) {
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
                    Text("Manage Answers", color = TextWhite, fontWeight = FontWeight.Bold)
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
                onValueChange = { viewModel.onAction(ManageAnswersAction.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search answers...", color = TextGray) },
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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
                uiState.filteredAnswers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No answers found", color = QOnSurfaceVariant, fontSize = 16.sp)
                    }
                }
                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.onAction(ManageAnswersAction.Refresh) },
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
                            items(uiState.filteredAnswers, key = { it.id }) { answer ->
                                AnswerCard(
                                    answer = answer,
                                    authorName = uiState.authorNames[answer.accountId] ?: "Unknown",
                                    questionTitle = uiState.questionTitles[answer.questionId] ?: "Unknown",
                                    onAnswerClick = { onAnswerClick(answer.questionId) },
                                    onDelete = { viewModel.onAction(ManageAnswersAction.DeleteAnswer(answer)) },
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

@Composable
private fun AnswerCard(
    answer: Answer,
    authorName: String,
    questionTitle: String,
    onAnswerClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAnswerClick() },
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = answer.description,
                color = TextWhite,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = authorName,
                color = TextGray,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = questionTitle,
                color = CyanPrimary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val votesCount = answer.votedIds
                        .split(",")
                        .filter { it.isNotBlank() }
                        .size

                    Icon(
                        Icons.Filled.ThumbUp,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$votesCount",
                        color = CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    if (answer.accepted) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Accepted",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QError),
                    border = BorderStroke(1.dp, QError.copy(alpha = 0.5f)),
                ) {
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
