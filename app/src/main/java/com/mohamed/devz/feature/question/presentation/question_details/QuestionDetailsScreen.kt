package com.mohamed.devz.feature.question.presentation.question_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerInputBar
import com.mohamed.devz.feature.question.presentation.question_details.components.QuestionContent
import com.mohamed.devz.feature.question.presentation.question_details.components.TopBar
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.report.presentation.ReportSheet
import com.mohamed.devz.feature.report.presentation.ReportTarget
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

data class QuestionDetailUiModel(
    val title: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val authorAccountId: Int,
    val timeAgo: String,
    val tags: List<String>,
    val body: String,
    val language: SyntaxLanguage,
    val code: String,
    val likes: Int,
    val answersCount: Int,
    val isLiked: Boolean = false,
    val likedAccountIds: String = "",
    val isPinned: Boolean = false,
    val isAuthorPro: Boolean = false,
)

@Composable
fun QuestionDetailScreen(
    questionId: Int,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToProfile: (Int) -> Unit = {},
    onEditQuestion: (Int) -> Unit = {},
    viewModel: QuestionDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(questionId) {
        viewModel.onAction(QuestionDetailsAction.LoadQuestion(questionId))
    }

    LaunchedEffect(Unit) {
        viewModel.questionDetailsEvent.collect { event ->
            when (event) {
                is QuestionDetailsViewModel.QuestionDetailsEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                val questionForReport = uiState.question
                TopBar(
                    onReport = if (questionForReport != null
                        && uiState.currentAccountId != 0
                        && questionForReport.authorAccountId != uiState.currentAccountId
                    ) {
                        {
                            viewModel.onAction(
                                QuestionDetailsAction.ShowReport(
                                    ReportTarget(
                                        reportedType = "question",
                                        reportedId = questionId,
                                        preview = questionForReport.title,
                                    )
                                )
                            )
                        }
                    } else null,
                )

                if (uiState.question == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.error != null) {
                            ErrorState(
                                message = uiState.error!!.asString(),
                                onRetry = {
                                    viewModel.onAction(QuestionDetailsAction.LoadQuestion(questionId))
                                },
                                retryLabel = stringResource(R.string.action_retry),
                            )
                        } else {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    val question = uiState.question!!
                    QuestionContent(
                        question = question,
                        answers = uiState.answers,
                        currentAccountId = uiState.currentAccountId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        navigateUp = navigateUp,
                        onLikeClick = { viewModel.onAction(QuestionDetailsAction.ToggleLike) },
                        onAnswerVoteClick = { answerId ->
                            viewModel.onAction(QuestionDetailsAction.ToggleAnswerVote(answerId))
                        },
                        onAcceptAnswer = { answerId ->
                            viewModel.onAction(QuestionDetailsAction.AcceptAnswer(answerId))
                        },
                        onQuestionAuthorClick = { onNavigateToProfile(question.authorAccountId) },
                        onAnswerAuthorClick = { accountId -> onNavigateToProfile(accountId) },
                        onCodeLongPress = { code ->
                            viewModel.onAction(QuestionDetailsAction.PrefillAnswerCode(code))
                        },
                        onAnswerReport = { answer ->
                            viewModel.onAction(
                                QuestionDetailsAction.ShowReport(
                                    ReportTarget(
                                        reportedType = "answer",
                                        reportedId = answer.answerId,
                                        preview = answer.body,
                                    )
                                )
                            )
                        },
                        isPro = uiState.isPro,
                        isPinned = question.isPinned,
                        isPinning = uiState.isPinning,
                        onPinClick = { viewModel.onAction(QuestionDetailsAction.PinQuestion) },
                        onEditClick = { onEditQuestion(questionId) },
                    )

                    if (!uiState.isCompany) {
                        AnswerInputBar(
                            answerText = uiState.answerText,
                            onAnswerChange = { viewModel.onAction(QuestionDetailsAction.AnswerTextChanged(it)) },
                            onPost = { viewModel.onAction(QuestionDetailsAction.PostAnswer({})) },
                            answerCode = uiState.answerCode,
                            onCodeClick = {
                                viewModel.onAction(QuestionDetailsAction.ShowCodeEditor)
                            }
                        )
                    }
                }
            }

            if (uiState.showCodeEditor) {
                CodeEditorOverlay(
                    code = uiState.answerCode ?: "",
                    onCodeChange = { viewModel.onAction(QuestionDetailsAction.AnswerCodeChanged(it)) },
                    onDone = { viewModel.onAction(QuestionDetailsAction.HideCodeEditor) },
                    onClear = {
                        viewModel.onAction(QuestionDetailsAction.AnswerCodeChanged(null))
                        viewModel.onAction(QuestionDetailsAction.HideCodeEditor)
                    },
                )
            }

            uiState.reportTarget?.let { target ->
                ReportSheet(
                    target = target,
                    onDismiss = { viewModel.onAction(QuestionDetailsAction.DismissReport) },
                )
            }
        }
    }
}

@Composable
private fun CodeEditorOverlay(
    code: String,
    onCodeChange: (String) -> Unit,
    onDone: () -> Unit,
    onClear: () -> Unit,
) {
    var text by remember(code) { mutableStateOf(code) }
    val extended = MaterialTheme.extendedColors

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = extended.codeBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onClear) {
                        Text(
                            stringResource(R.string.details_clear),
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = stringResource(R.string.details_answer_code_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextButton(onClick = {
                        onCodeChange(text)
                        onDone()
                    }) {
                        Text(
                            stringResource(R.string.details_done),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        textStyle = TextStyle(
                            color = extended.codeText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    )

                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.details_code_hint),
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewQuestionDetailScreen() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        QuestionDetailScreen(
            questionId = 1,
            navigateUp = {}
        )
    }
}
