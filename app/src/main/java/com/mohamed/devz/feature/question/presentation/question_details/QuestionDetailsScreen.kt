package com.mohamed.devz.feature.question.presentation.question_details

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerInputBar
import com.mohamed.devz.feature.question.presentation.question_details.components.QuestionContent
import com.mohamed.devz.feature.question.presentation.question_details.components.TopBar
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary

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
        modifier = modifier.background(QBg)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar()

                if (uiState.question == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.error != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = uiState.error!!.asString(),
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(onClick = {
                                    viewModel.onAction(QuestionDetailsAction.LoadQuestion(questionId))
                                }) {
                                    Text("Retry")
                                }
                            }
                        } else {
                            CircularProgressIndicator(color = CyanPrimary)
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
                        isPro = uiState.isPro,
                        isPinned = question.isPinned,
                        isPinning = uiState.isPinning,
                        onPinClick = { viewModel.onAction(QuestionDetailsAction.PinQuestion) },
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0E0E0E)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onClear) {
                        Text("Clear", color = QOutline, fontSize = 14.sp)
                    }
                    Text(
                        text = "Answer Code",
                        color = QOnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextButton(onClick = {
                        onCodeChange(text)
                        onDone()
                    }) {
                        Text("Done", color = QPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    textStyle = TextStyle(
                        color = Color(0xFFD4D4D4),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(QPrimary),
                )

                if (text.isEmpty()) {
                    Text(
                        text = "Paste or write your code here...",
                        color = Color(0xFF555555),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewQuestionDetailScreen() {
    DevzTheme {
        QuestionDetailScreen(
            questionId = 1,
            navigateUp = {}
        )
    }
}
