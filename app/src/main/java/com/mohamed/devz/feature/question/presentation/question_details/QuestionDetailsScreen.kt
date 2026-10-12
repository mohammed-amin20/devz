package com.mohamed.devz.feature.question.presentation.question_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerUiModel
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

    QuestionDetailContent(
        state = uiState,
        questionId = questionId,
        navigateUp = navigateUp,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onNavigateToProfile = onNavigateToProfile,
        onEditQuestion = onEditQuestion,
        onRetryLoad = { viewModel.onAction(QuestionDetailsAction.LoadQuestion(questionId)) },
        onLikeClick = { viewModel.onAction(QuestionDetailsAction.ToggleLike) },
        onAnswerVoteClick = { viewModel.onAction(QuestionDetailsAction.ToggleAnswerVote(it)) },
        onAcceptAnswer = { viewModel.onAction(QuestionDetailsAction.AcceptAnswer(it)) },
        onCodeLongPress = { viewModel.onAction(QuestionDetailsAction.PrefillAnswerCode(it)) },
        onShowReport = { viewModel.onAction(QuestionDetailsAction.ShowReport(it)) },
        onDismissReport = { viewModel.onAction(QuestionDetailsAction.DismissReport) },
        onPinClick = { viewModel.onAction(QuestionDetailsAction.PinQuestion) },
        onAnswerTextChange = { viewModel.onAction(QuestionDetailsAction.AnswerTextChanged(it)) },
        onPostAnswer = { viewModel.onAction(QuestionDetailsAction.PostAnswer({})) },
        onCodeEditorClick = { viewModel.onAction(QuestionDetailsAction.ShowCodeEditor) },
        onCodeChange = { viewModel.onAction(QuestionDetailsAction.AnswerCodeChanged(it)) },
        onCodeDone = { viewModel.onAction(QuestionDetailsAction.HideCodeEditor) },
        onCodeClear = {
            viewModel.onAction(QuestionDetailsAction.AnswerCodeChanged(null))
            viewModel.onAction(QuestionDetailsAction.HideCodeEditor)
        },
    )
}

@Composable
fun QuestionDetailContent(
    state: QuestionDetailsState,
    questionId: Int,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateToProfile: (Int) -> Unit,
    onEditQuestion: (Int) -> Unit,
    onRetryLoad: () -> Unit,
    onLikeClick: () -> Unit,
    onAnswerVoteClick: (Int) -> Unit,
    onAcceptAnswer: (Int) -> Unit,
    onCodeLongPress: (String) -> Unit,
    onShowReport: (ReportTarget) -> Unit,
    onDismissReport: () -> Unit,
    onPinClick: () -> Unit,
    onAnswerTextChange: (String) -> Unit,
    onPostAnswer: () -> Unit,
    onCodeEditorClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onCodeDone: () -> Unit,
    onCodeClear: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                val questionForReport = state.question
                TopBar(
                    onReport = if (questionForReport != null
                        && state.currentAccountId != 0
                        && questionForReport.authorAccountId != state.currentAccountId
                    ) {
                        {
                            onShowReport(
                                ReportTarget(
                                    reportedType = "question",
                                    reportedId = questionId,
                                    preview = questionForReport.title,
                                )
                            )
                        }
                    } else null,
                )

                if (state.question == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.error != null) {
                            ErrorState(
                                message = state.error!!.asString(),
                                onRetry = onRetryLoad,
                                retryLabel = stringResource(R.string.action_retry),
                            )
                        } else {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    val question = state.question!!
                    QuestionContent(
                        question = question,
                        answers = state.answers,
                        currentAccountId = state.currentAccountId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        navigateUp = navigateUp,
                        onLikeClick = onLikeClick,
                        onAnswerVoteClick = onAnswerVoteClick,
                        onAcceptAnswer = onAcceptAnswer,
                        onQuestionAuthorClick = { onNavigateToProfile(question.authorAccountId) },
                        onAnswerAuthorClick = onNavigateToProfile,
                        onCodeLongPress = onCodeLongPress,
                        onAnswerReport = { answer ->
                            onShowReport(
                                ReportTarget(
                                    reportedType = "answer",
                                    reportedId = answer.answerId,
                                    preview = answer.body,
                                )
                            )
                        },
                        isPro = state.isPro,
                        isPinned = question.isPinned,
                        isPinning = state.isPinning,
                        onPinClick = onPinClick,
                        onEditClick = { onEditQuestion(questionId) },
                    )

                    if (!state.isCompany) {
                        AnswerInputBar(
                            answerText = state.answerText,
                            onAnswerChange = onAnswerTextChange,
                            onPost = onPostAnswer,
                            answerCode = state.answerCode,
                            onCodeClick = onCodeEditorClick,
                        )
                    }
                }
            }

            if (state.showCodeEditor) {
                CodeEditorOverlay(
                    code = state.answerCode ?: "",
                    onCodeChange = onCodeChange,
                    onDone = onCodeDone,
                    onClear = onCodeClear,
                )
            }

            state.reportTarget?.let { target ->
                ReportSheet(
                    target = target,
                    onDismiss = onDismissReport,
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
        QuestionDetailContent(
            state = QuestionDetailsState(
                question = previewQuestionDetail,
                answers = listOf(previewAnswer),
                isLoading = false,
                currentAccountId = 5,
            ),
            questionId = 1,
            navigateUp = {},
            snackbarHostState = SnackbarHostState(),
            onNavigateToProfile = {},
            onEditQuestion = {},
            onRetryLoad = {},
            onLikeClick = {},
            onAnswerVoteClick = {},
            onAcceptAnswer = {},
            onCodeLongPress = {},
            onShowReport = {},
            onDismissReport = {},
            onPinClick = {},
            onAnswerTextChange = {},
            onPostAnswer = {},
            onCodeEditorClick = {},
            onCodeChange = {},
            onCodeDone = {},
            onCodeClear = {},
        )
    }
}

private val previewQuestionDetail = QuestionDetailUiModel(
    title = "How to handle deep links in Compose Navigation?",
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    authorAccountId = 10,
    timeAgo = "2h ago",
    tags = listOf("compose", "navigation"),
    body = "I'm trying to navigate to a specific screen using a deep link but it keeps opening the Home screen.",
    language = SyntaxLanguage.KOTLIN,
    code = "composable<Route.QuestionDetails>(\n    deepLinks = listOf(navDeepLink { uriPattern = \"devz://question/{id}\" })\n)",
    likes = 12,
    answersCount = 3,
    isAuthorPro = true,
)

private val previewAnswer = AnswerUiModel(
    answerId = 1,
    authorName = "Sara Khaled",
    avatarUrl = "",
    authorAccountId = 11,
    body = "Make sure your deep link URI matches the pattern exactly, including the scheme.",
    isAccepted = true,
    likes = 5,
    timeAgo = "1h ago",
    code = "android:host=\"question\"",
    language = SyntaxLanguage.KOTLIN,
)