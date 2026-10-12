package com.mohamed.devz.feature.notification.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.components.DevzBrandHeader
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun NotificationsScreen(
    onNotificationClick: (NotificationUiModel) -> Unit = {},
    onUnreadChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val notifications = uiState.notifications
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onAction(NotificationsAction.Refresh)
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevzBrandHeader()
                if (notifications.any { !it.isRead }) {
                    TextButton(
                        onClick = {
                            viewModel.onAction(NotificationsAction.MarkAllRead)
                            onUnreadChanged()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.notif_mark_all_read),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            val pullRefreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.onAction(NotificationsAction.Refresh)
                },
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isRefreshing,
                        state = pullRefreshState,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                when {
                    uiState.isLoading && notifications.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    uiState.error != null && notifications.isEmpty() -> {
                        ErrorState(
                            message = uiState.error!!.asString(),
                            onRetry = { viewModel.onAction(NotificationsAction.Refresh) },
                            retryLabel = stringResource(R.string.action_retry),
                        )
                    }

                    !uiState.isLoading && notifications.isEmpty() -> {
                        EmptyState(
                            icon = Icons.Filled.NotificationsNone,
                            title = stringResource(R.string.notif_empty_title),
                            message = stringResource(R.string.notif_empty_message),
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }

                            items(notifications, key = { it.id }) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onClick = {
                                        viewModel.onAction(NotificationsAction.MarkRead(notification.id))
                                        onUnreadChanged()
                                        onNotificationClick(notification)
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationUiModel,
    onClick: () -> Unit = {},
) {
    val isUnread = !notification.isRead
    val isSystem = notification.senderType == "system"
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    isSystem -> primary.copy(alpha = 0.08f)
                    isUnread -> MaterialTheme.colorScheme.surfaceContainerHigh
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSystem -> primary.copy(alpha = 0.2f)
                        isUnread -> primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceContainerHighest
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSystem) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.ACCEPTED -> Icons.Filled.CheckCircle
                        NotificationType.UPVOTE -> Icons.Filled.ArrowUpward
                        NotificationType.LIKE -> Icons.Filled.FavoriteBorder
                        NotificationType.ANSWER -> Icons.Filled.ChatBubbleOutline
                        NotificationType.FOLLOWER -> Icons.Filled.Person
                        NotificationType.SYSTEM -> Icons.Filled.Campaign
                    },
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            if (isSystem) {
                val parts = notification.message.split("\n", limit = 2)
                val title = parts.getOrElse(0) { "" }
                val body = parts.getOrElse(1) { "" }
                Text(
                    text = stringResource(R.string.notif_system_brand),
                    color = primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (title.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        color = onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (body.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = body,
                        color = onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(primary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.notif_system_badge),
                            color = primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = notification.timeAgo,
                        color = onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }
            } else {
                val someone = stringResource(R.string.notif_someone)
                val congrats = stringResource(R.string.notif_congrats)
                Text(
                    text = buildAnnotatedString {
                        when {
                            notification.type == NotificationType.FOLLOWER -> {
                                withStyle(SpanStyle(color = onSurface, fontWeight = FontWeight.Bold)) {
                                    append(notification.actorName ?: someone)
                                }
                                withStyle(SpanStyle(color = onSurfaceVariant)) {
                                    append(" ${notification.message}")
                                }
                            }
                            notification.actorName == null -> {
                                withStyle(SpanStyle(color = onSurface, fontWeight = FontWeight.Bold)) {
                                    append("$congrats ")
                                }
                                withStyle(SpanStyle(color = onSurfaceVariant)) {
                                    append("${notification.message} ${notification.questionTitle}")
                                }
                            }
                            else -> {
                                withStyle(SpanStyle(color = onSurface, fontWeight = FontWeight.Bold)) {
                                    append("${notification.actorName} ")
                                }
                                withStyle(SpanStyle(color = onSurfaceVariant)) {
                                    append("${notification.message} ${notification.questionTitle}")
                                }
                            }
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
                    color = if (isUnread) primary else onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isUnread && !isSystem) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(primary)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewNotifications() {
    DevzTheme {
        NotificationsScreen()
    }
}
