package com.mohamed.devz.feature.admin.presentation.manage_announcements

import com.mohamed.devz.feature.core.domain.model.Notification

sealed interface ManageAnnouncementsAction {
    data class TitleChanged(val value: String) : ManageAnnouncementsAction
    data class MessageChanged(val value: String) : ManageAnnouncementsAction
    data object ShowCreateDialog : ManageAnnouncementsAction
    data object DismissCreateDialog : ManageAnnouncementsAction
    data object CreateAnnouncement : ManageAnnouncementsAction
    data class DeleteAnnouncement(val notification: Notification) : ManageAnnouncementsAction
    data class ConfirmDelete(val notification: Notification) : ManageAnnouncementsAction
    data object DismissDeleteDialog : ManageAnnouncementsAction
    data object Refresh : ManageAnnouncementsAction
}
