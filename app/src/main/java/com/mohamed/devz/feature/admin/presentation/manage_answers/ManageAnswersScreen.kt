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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnswersScreen(
    onNavigateUp: () -> Unit,
    onAnswerClick: (Int) -> Unit,
    viewModel: ManageAnswersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ManageAnswersContent(
        state = uiState,
        onNavigateUp = onNavigateUp,
        onAnswerClick = onAnswerClick,
        onSearchQueryChanged = { viewModel.onAction(ManageAnswersAction.SearchQueryChanged(it)) },
        onRefresh = { viewModel.onAction(ManageAnswersAction.Refresh) },
        onDeleteAnswer = { viewModel.onAction(ManageAnswersAction.DeleteAnswer(it)) },
        onConfirmDelete = { viewModel.onAction(ManageAnswersAction.ConfirmDelete(it)) },
        onDismissDeleteDialog = { viewModel.onAction(ManageAnswersAction.DismissDeleteDialog) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnswersContent(
    state: ManageAnswersState,
    onNavigateUp: () -> Unit,
    onAnswerClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRefresh: () -> Unit,
    onDeleteAnswer: (Answer) -> Unit,
    onConfirmDelete: (Answer) -> Unit,
    onDismissDeleteDialog: () -> Unit,
) {
    if (state.showDeleteDialog && state.targetDeleteAnswer != null) {
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_delete_answer_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_delete_answer_message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmDelete(state.targetDeleteAnswer!!) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) {
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
                        stringResource(R.string.admin_answers_title),
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            DevzTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.admin_search_answers),
                leadingIcon = Icons.Filled.Search,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                state.filteredAnswers.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Filled.Search,
                        title = stringResource(R.string.admin_empty_answers),
                    )
                }
                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = onRefresh,
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize(),
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
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(state.filteredAnswers, key = { it.id }) { answer ->
                                AnswerCard(
                                    answer = answer,
                                    authorName = state.authorNames[answer.accountId]
                                        ?: stringResource(R.string.admin_unknown),
                                    questionTitle = state.questionTitles[answer.questionId]
                                        ?: stringResource(R.string.admin_unknown),
                                    onAnswerClick = { onAnswerClick(answer.questionId) },
                                    onDelete = { onDeleteAnswer(answer) },
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
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = answer.description,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = authorName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = questionTitle,
                color = MaterialTheme.colorScheme.primary,
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$votesCount",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    if (answer.accepted) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.success,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.admin_accepted),
                            color = MaterialTheme.extendedColors.success,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text(stringResource(R.string.action_delete), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageAnswersDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageAnswersContent(
            state = ManageAnswersState(
                filteredAnswers = listOf(previewAnswer),
                authorNames = mapOf(1 to "Sara Ali"),
                questionTitles = mapOf(10 to "How to scope coroutines?"),
                isLoading = false,
            ),
            onNavigateUp = {},
            onAnswerClick = {},
            onSearchQueryChanged = {},
            onRefresh = {},
            onDeleteAnswer = {},
            onConfirmDelete = {},
            onDismissDeleteDialog = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageAnswersLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageAnswersContent(
            state = ManageAnswersState(
                filteredAnswers = listOf(previewAnswer),
                authorNames = mapOf(1 to "Sara Ali"),
                questionTitles = mapOf(10 to "How to scope coroutines?"),
                isLoading = false,
            ),
            onNavigateUp = {},
            onAnswerClick = {},
            onSearchQueryChanged = {},
            onRefresh = {},
            onDeleteAnswer = {},
            onConfirmDelete = {},
            onDismissDeleteDialog = {},
        )
    }
}

private val previewAnswer = Answer(
    id = 1,
    description = "Use viewModelScope and collect state in a LaunchedEffect.",
    accepted = true,
    votedIds = "1,2,3",
    questionId = 10,
    accountId = 1,
    createdAt = "2024-01-01T00:00:00Z",
)