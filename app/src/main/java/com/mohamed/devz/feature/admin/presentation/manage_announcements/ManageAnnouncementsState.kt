package com.mohamed.devz.feature.admin.presentation.manage_announcements

import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ManageAnnouncementsState(
    val announcements: List<Notification> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val showCreateDialog: Boolean = false,
    val createTitle: String = "",
    val createMessage: String = "",
    val showDeleteDialog: Boolean = false,
    val targetDeleteNotification: Notification? = null,
)
