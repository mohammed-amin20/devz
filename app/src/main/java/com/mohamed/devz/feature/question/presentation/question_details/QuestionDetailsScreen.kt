package com.mohamed.devz.feature.question.presentation.question_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerInputBar
import com.mohamed.devz.feature.question.presentation.question_details.components.QuestionContent
import com.mohamed.devz.feature.question.presentation.question_details.components.TopBar
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.QBg

data class QuestionDetailUiModel(
    val title: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val timeAgo: String,
    val tags: List<String>,
    val body: String,
    val language: SyntaxLanguage,
    val code: String,
    val likes: Int,
    val answersCount: Int
)

@Composable
fun QuestionDetailScreen(
    questionId: Int,
    viewModel: QuestionDetailsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(questionId) {
        viewModel.onAction(QuestionDetailsAction.LoadQuestion(questionId))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QBg)
            .then(modifier)
    ) {
        TopBar()

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanPrimary)
            }
        } else {
            QuestionContent(
                question = uiState.question,
                answers = uiState.answers,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            AnswerInputBar(
                answerText = uiState.answerText,
                onAnswerChange = { viewModel.onAction(QuestionDetailsAction.AnswerTextChanged(it)) },
                onPost = { viewModel.onAction(QuestionDetailsAction.PostAnswer({})) }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewQuestionDetailScreen() {
    DevzTheme {
        QuestionDetailScreen(
            questionId = 1
        )
    }
}
