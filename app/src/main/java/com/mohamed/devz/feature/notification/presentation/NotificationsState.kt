package com.mohamed.devz.feature.notification.presentation

import com.mohamed.devz.feature.notification.presentation.util.NotificationUiModel

data class NotificationsState(
    val notifications: List<NotificationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)