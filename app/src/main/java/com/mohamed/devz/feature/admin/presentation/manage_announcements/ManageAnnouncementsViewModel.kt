package com.mohamed.devz.feature.admin.presentation.manage_announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAnnouncementsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAnnouncementsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    fun onAction(action: ManageAnnouncementsAction) {
        when (action) {
            is ManageAnnouncementsAction.TitleChanged -> {
                _uiState.update { it.copy(createTitle = action.value) }
            }
            is ManageAnnouncementsAction.MessageChanged -> {
                _uiState.update { it.copy(createMessage = action.value) }
            }
            is ManageAnnouncementsAction.ShowCreateDialog -> {
                _uiState.update {
                    it.copy(showCreateDialog = true, createTitle = "", createMessage = "")
                }
            }
            is ManageAnnouncementsAction.DismissCreateDialog -> {
                _uiState.update {
                    it.copy(showCreateDialog = false, createTitle = "", createMessage = "")
                }
            }
            is ManageAnnouncementsAction.CreateAnnouncement -> {
                createAnnouncement()
            }
            is ManageAnnouncementsAction.DeleteAnnouncement -> {
                _uiState.update {
                    it.copy(
                        showDeleteDialog = true,
                        targetDeleteNotification = action.notification
                    )
                }
            }
            is ManageAnnouncementsAction.ConfirmDelete -> {
                deleteAnnouncement(action.notification)
            }
            is ManageAnnouncementsAction.DismissDeleteDialog -> {
                _uiState.update {
                    it.copy(showDeleteDialog = false, targetDeleteNotification = null)
                }
            }
            is ManageAnnouncementsAction.Refresh -> loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = notificationRepository.getSystemNotifications()) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }
                    _uiState.update {
                        it.copy(announcements = sorted, isLoading = false)
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUIText(),
                        )
                    }
                }
            }
        }
    }

    private fun createAnnouncement() {
        val state = _uiState.value
        if (state.createTitle.isBlank() || state.createMessage.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(showCreateDialog = false) }

            val notification = Notification(
                id = 0,
                typeId = 0,
                userId = 0,
                actorId = 0,
                questionId = 0,
                answerId = null,
                type = "system",
                message = "${state.createTitle}\n${state.createMessage}",
                isRead = false,
                createdAt = "",
                senderType = "system",
                isGlobal = true,
            )

            when (val result = notificationRepository.insert(notification)) {
                is Result.Success -> {
                    fcmPushSender.sendPushToTopic(
                        topic = "announcements",
                        title = state.createTitle,
                        body = state.createMessage,
                        type = "system",
                    )
                    loadAnnouncements()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun deleteAnnouncement(notification: Notification) {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = false, targetDeleteNotification = null) }

            val deleted = notification.copy(isRead = true)
            when (val result = notificationRepository.update(deleted)) {
                is Result.Success -> loadAnnouncements()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
