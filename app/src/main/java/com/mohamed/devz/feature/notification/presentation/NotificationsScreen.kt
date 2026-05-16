package com.mohamed.devz.feature.notification.presentation

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.feature.question.presentation.question_details.components.Bg
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier
) {
    val notifications = sampleNotifications

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .then(modifier)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    color = CyanPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // ── List ──────────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(notifications, key = { it.id }) { notification ->
                    NotificationItem(notification = notification)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ── Notification Item ─────────────────────────────────────────────────────────
@Composable
fun NotificationItem(notification: NotificationUiModel) {
    val isUnread = !notification.isRead

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isUnread) Color(0xFF1A2424) else Color(0xFF141E1E))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isUnread) CyanPrimary.copy(alpha = 0.15f)
                    else Color(0xFF1E2A2A)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (notification.type) {
                    NotificationType.ACCEPTED -> Icons.Filled.CheckCircle
                    NotificationType.UPVOTE -> Icons.Filled.ArrowUpward
                    NotificationType.LIKE -> Icons.Filled.FavoriteBorder
                    NotificationType.COMMENT -> Icons.Filled.ChatBubbleOutline
                },
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    if (notification.actorName == null) {
                        withStyle(SpanStyle(color = TextWhite, fontWeight = FontWeight.Bold)) {
                            append("Congrats! ")
                        }
                    } else {
                        withStyle(SpanStyle(color = TextWhite, fontWeight = FontWeight.Bold)) {
                            append("${notification.actorName} ")
                        }
                    }
                    withStyle(SpanStyle(color = TextGray)) {
                        append("${notification.message} ${notification.questionTitle}")
                    }
                },
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.timeAgo,
                color = if (isUnread) CyanPrimary else TextGray,
                fontSize = 12.sp,
                fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
            )
        }
    }
}

// ── Notification types ────────────────────────────────────────────────────────
enum class NotificationType {
    ACCEPTED,
    UPVOTE,
    LIKE,
    COMMENT
}

// ── Notification model ────────────────────────────────────────────────────────
data class NotificationUiModel(
    val id: String,
    val type: NotificationType,
    val actorName: String?,
    val message: String,
    val questionTitle: String,
    val timeAgo: String,
    val isRead: Boolean = false,
)

// ── UiState ───────────────────────────────────────────────────────────────────
data class NotificationsUiState(
    val notifications: List<NotificationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

// ── Actions ───────────────────────────────────────────────────────────────────
sealed class NotificationsAction {
    object MarkAllRead : NotificationsAction()
    data class MarkRead(val id: String) : NotificationsAction()
}

// ── Sample data ───────────────────────────────────────────────────────────────
val sampleNotifications = listOf(
    NotificationUiModel(
        id = "1",
        type = NotificationType.ACCEPTED,
        actorName = null,
        message = "Your answer was chosen as the best in",
        questionTitle = "How to implement clean architecture...",
        timeAgo = "2m ago",
        isRead = false
    ),
    NotificationUiModel(
        id = "2",
        type = NotificationType.UPVOTE,
        actorName = null,
        message = "Your answer received 26 upvotes in",
        questionTitle = "How to implement firestore pagination...",
        timeAgo = "1h ago",
        isRead = false
    ),
    NotificationUiModel(
        id = "3",
        type = NotificationType.LIKE,
        actorName = "John Doe",
        message = "liked on your question in",
        questionTitle = "How to implement firestore....",
        timeAgo = "1h ago",
        isRead = true
    ),
    NotificationUiModel(
        id = "4",
        type = NotificationType.COMMENT,
        actorName = "John Doe",
        message = "commented on your question in",
        questionTitle = "How to implement ...",
        timeAgo = "1h ago",
        isRead = true
    ),
    NotificationUiModel(
        id = "5",
        type = NotificationType.UPVOTE,
        actorName = "John Doe",
        message = "upvoted on your answer in",
        questionTitle = "How to implement fir...",
        timeAgo = "2d ago",
        isRead = true
    ),
    NotificationUiModel(
        id = "6",
        type = NotificationType.UPVOTE,
        actorName = "John Doe",
        message = "upvoted on your answer in",
        questionTitle = "How to implement fir...",
        timeAgo = "3d ago",
        isRead = true
    ),
    NotificationUiModel(
        id = "7",
        type = NotificationType.COMMENT,
        actorName = "John Doe",
        message = "commented on your question in",
        questionTitle = "How to implement ...",
        timeAgo = "1w ago",
        isRead = true
    ),
    NotificationUiModel(
        id = "8",
        type = NotificationType.COMMENT,
        actorName = "John Doe",
        message = "commented on your question in",
        questionTitle = "How to implement ...",
        timeAgo = "1w ago",
        isRead = true
    )
)


@Preview(showSystemUi = true)
@Composable
private fun PreviewNotifications() {
    DevzTheme {
        NotificationsScreen()
    }
}

