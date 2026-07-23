package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailUiModel
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionContent(
    question: QuestionDetailUiModel,
    answers: List<AnswerUiModel>,
    currentAccountId: Int,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onAnswerVoteClick: (Int) -> Unit = {},
    onAcceptAnswer: (Int) -> Unit = {},
    onQuestionAuthorClick: () -> Unit = {},
    onAnswerAuthorClick: (Int) -> Unit = {},
    onCodeLongPress: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 0.dp, start = 16.dp, end = 16.dp),
        contentPadding = PaddingValues(
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Breadcrumb(
                questionTitle = question.title,
                navigateUp = navigateUp
            )
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text(
                text = question.title,
                color = QOnSurface,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(18.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onQuestionAuthorClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = question.authorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, QPrimary.copy(alpha = 0.2f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = question.authorName,
                            color = QOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Asked ${question.timeAgo}",
                            color = QOutline,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    question.tags.forEach { tag ->
                        TagChip(text = tag)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Text(
                text = question.body,
                color = QOnSurfaceVariant,
                fontSize = 16.sp,
                lineHeight = 26.sp,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(18.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { onCodeLongPress(question.code) }
                    )
            ) {
                CodeBlock(
                    code = question.code,
                    language = question.language
                )
            }
            Spacer(Modifier.height(22.dp))
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionPill(
                    icon = Icons.Default.ThumbUp,
                    text = question.likes.toString(),
                    active = question.isLiked,
                    onClick = onLikeClick,
                )
                ActionPill(
                    icon = Icons.Default.ModeComment,
                    text = question.answersCount.toString(),
                    active = false,
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(QPrimary.copy(alpha = 0.18f))
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Text(
                text = "${answers.size} Answers",
                color = QOnSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
        }

        items(answers.size) { index ->
            AnswerCard(
                answer = answers[index],
                onVoteClick = { onAnswerVoteClick(answers[index].answerId) },
                isAcceptButtonVisible = currentAccountId == question.authorAccountId
                        && currentAccountId != answers[index].authorAccountId,
                onAcceptClick = { onAcceptAnswer(answers[index].answerId) },
                onAuthorClick = { onAnswerAuthorClick(answers[index].authorAccountId) },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
