package com.mohamed.devz.feature.notification.presentation.util

data class NotificationUiModel(
    val id: String,
    val type: NotificationType,
    val actorName: String?,
    val message: String,
    val questionTitle: String,
    val timeAgo: String,
    val isRead: Boolean = false,
)