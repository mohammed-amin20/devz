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
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageQuestionsScreen(
    onNavigateUp: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    viewModel: ManageQuestionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ManageQuestionsContent(
        state = uiState,
        onNavigateUp = onNavigateUp,
        onQuestionClick = onQuestionClick,
        onSearchQueryChanged = { viewModel.onAction(ManageQuestionsAction.SearchQueryChanged(it)) },
        onFilterAll = { viewModel.onAction(ManageQuestionsAction.FilterAll) },
        onFilterHidden = { viewModel.onAction(ManageQuestionsAction.FilterHidden) },
        onRefresh = { viewModel.onAction(ManageQuestionsAction.Refresh) },
        onHideQuestion = { viewModel.onAction(ManageQuestionsAction.HideQuestion(it)) },
        onUnhideQuestion = { viewModel.onAction(ManageQuestionsAction.UnhideQuestion(it)) },
        onConfirmHide = { viewModel.onAction(ManageQuestionsAction.ConfirmHide(it)) },
        onDismissHideDialog = { viewModel.onAction(ManageQuestionsAction.DismissHideDialog) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageQuestionsContent(
    state: ManageQuestionsState,
    onNavigateUp: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterAll: () -> Unit,
    onFilterHidden: () -> Unit,
    onRefresh: () -> Unit,
    onHideQuestion: (Question) -> Unit,
    onUnhideQuestion: (Question) -> Unit,
    onConfirmHide: (Question) -> Unit,
    onDismissHideDialog: () -> Unit,
) {
    if (state.showHideDialog && state.targetHideQuestion != null) {
        AlertDialog(
            onDismissRequest = onDismissHideDialog,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_hide_question_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_hide_question_message, state.targetHideQuestion!!.title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmHide(state.targetHideQuestion!!) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(R.string.action_hide))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissHideDialog) {
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
                        stringResource(R.string.admin_questions_title),
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
                placeholder = stringResource(R.string.admin_search_questions),
                leadingIcon = Icons.Filled.Search,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.selectedFilter == 0,
                    onClick = onFilterAll,
                    label = { Text(stringResource(R.string.admin_filter_all), fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        enabled = true,
                        selected = state.selectedFilter == 0,
                    ),
                )
                FilterChip(
                    selected = state.selectedFilter == 1,
                    onClick = onFilterHidden,
                    label = { Text(stringResource(R.string.admin_filter_hidden), fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.error,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                        enabled = true,
                        selected = state.selectedFilter == 1,
                    ),
                )
            }

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
                state.filteredQuestions.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Filled.Search,
                        title = stringResource(R.string.admin_empty_questions),
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
                            items(state.filteredQuestions, key = { it.id }) { question ->
                                QuestionCard(
                                    question = question,
                                    authorName = state.authorNames[question.accountId]
                                        ?: stringResource(R.string.admin_unknown),
                                    onQuestionClick = { onQuestionClick(question.id) },
                                    onHide = { onHideQuestion(question) },
                                    onUnhide = { onUnhideQuestion(question) },
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
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = question.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (question.isHidden) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.admin_badge_hidden),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.admin_by_author, authorName),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
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
                        text = stringResource(R.string.admin_answers_count, question.answersCount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = stringResource(R.string.admin_likes_count, question.likesCount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }

                if (question.isHidden) {
                    OutlinedButton(
                        onClick = onUnhide,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    ) {
                        Text(stringResource(R.string.action_unhide), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onHide,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    ) {
                        Text(stringResource(R.string.action_hide), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageQuestionsDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageQuestionsContent(
            state = ManageQuestionsState(
                filteredQuestions = listOf(previewQuestion),
                authorNames = mapOf(1 to "Sara Ali"),
                selectedFilter = 0,
                isLoading = false,
            ),
            onNavigateUp = {},
            onQuestionClick = {},
            onSearchQueryChanged = {},
            onFilterAll = {},
            onFilterHidden = {},
            onRefresh = {},
            onHideQuestion = {},
            onUnhideQuestion = {},
            onConfirmHide = {},
            onDismissHideDialog = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageQuestionsLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageQuestionsContent(
            state = ManageQuestionsState(
                filteredQuestions = listOf(previewQuestion),
                authorNames = mapOf(1 to "Sara Ali"),
                selectedFilter = 0,
                isLoading = false,
            ),
            onNavigateUp = {},
            onQuestionClick = {},
            onSearchQueryChanged = {},
            onFilterAll = {},
            onFilterHidden = {},
            onRefresh = {},
            onHideQuestion = {},
            onUnhideQuestion = {},
            onConfirmHide = {},
            onDismissHideDialog = {},
        )
    }
}

private val previewQuestion = Question(
    id = 1,
    title = "How to scope coroutines in Jetpack Compose?",
    description = "I'm trying to understand best practices here.",
    code = "",
    likesCount = 12,
    answersCount = 4,
    tags = "Kotlin, Compose",
    langTypeId = 1,
    accountId = 1,
    createdAt = "2024-01-01T00:00:00Z",
)