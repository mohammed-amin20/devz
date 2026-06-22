package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QPrimary
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.QSurfaceLow

data class AnswerUiModel(
    val answerId: Int,
    val authorName: String,
    val avatarUrl: String,
    val authorAccountId: Int,
    val body: String,
    val isAccepted: Boolean,
    val likes: Int,
    val timeAgo: String,
    val isLiked: Boolean = false,
)
@Composable
fun AnswerCard(
    answer: AnswerUiModel,
    isAcceptButtonVisible: Boolean,
    onVoteClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (answer.isAccepted) QPrimary.copy(alpha = 0.3f) else Color.Transparent
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = if (answer.isAccepted) 3.dp else 0.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(QSurfaceLow)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = answer.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = answer.authorName,
                            color = QOnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = answer.timeAgo,
                            color = if (answer.isAccepted) QPrimary else QOutline,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (answer.isAccepted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = QPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Accepted",
                            color = QPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = answer.body,
                color = QOnSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 23.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    onClick = onVoteClick,
                    shape = RoundedCornerShape(10.dp),
                    color = if (answer.isLiked) QPrimary.copy(alpha = 0.15f) else QSurfaceHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = if (answer.isLiked) "Remove upvote" else "Upvote",
                            tint = if (answer.isLiked) QPrimary else QOutline,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = answer.likes.toString(),
                            color = if (answer.isLiked) QPrimary else QOutline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (isAcceptButtonVisible && !answer.isAccepted) {
                    Surface(
                        onClick = onAcceptClick,
                        shape = RoundedCornerShape(10.dp),
                        color = QPrimary.copy(alpha = 0.15f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Accept answer",
                                tint = QPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Accept",
                                color = QPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAnswerCard() {
    MaterialTheme {
        AnswerCard(
            answer = AnswerUiModel(
                answerId = 0,
                authorName = "Author",
                avatarUrl = "",
                authorAccountId = 0,
                body = "",
                isAccepted = true,
                likes = 10,
                timeAgo = "1h"
            ),
            isAcceptButtonVisible = true
        )
    }
}