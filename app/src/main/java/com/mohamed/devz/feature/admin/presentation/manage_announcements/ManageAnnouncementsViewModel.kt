package com.mohamed.devz.feature.admin.presentation.manage_announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationTypeRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAnnouncementsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val accountRepository: AccountRepository,
    private val notificationTypeRepository: NotificationTypeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
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
            is ManageAnnouncementsAction.Refresh -> loadAnnouncements(isRefresh = true)
        }
    }

    private fun loadAnnouncements(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }

            when (val result = notificationRepository.getSystemNotifications()) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }
                    _uiState.update {
                        it.copy(
                            announcements = sorted,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
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

            val adminId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (adminId == 0) {
                _uiState.update { it.copy(error = UiText.DynamicString("Admin not found")) }
                return@launch
            }

            val typeResult = notificationTypeRepository.getAll()
            val typeId = when (val result = typeResult) {
                is Result.Success -> result.data.firstOrNull { it.type.equals("system", true) }?.id
                    ?: result.data.firstOrNull()?.id
                is Result.Error -> null
            } ?: 1

            val message = "${state.createTitle}\n${state.createMessage}"
            val notification = Notification(
                id = 0,
                typeId = typeId,
                userId = adminId,
                actorId = adminId,
                questionId = 0,
                answerId = null,
                type = "system",
                message = message,
                isRead = false,
                createdAt = "",
                senderType = "system",
                isGlobal = true,
            )

            when (val result = notificationRepository.insert(notification)) {
                is Result.Success -> {
                    val accountsResult = accountRepository.getAll()
                    val developers = (accountsResult as? Result.Success)?.data
                        ?.filter { it.accountType != "company" && it.id != adminId } ?: emptyList()
                    developers.forEach { developer ->
                        val userNotification = Notification(
                            id = 0,
                            typeId = typeId,
                            userId = developer.id,
                            actorId = adminId,
                            questionId = 0,
                            answerId = null,
                            type = "system",
                            message = message,
                            isRead = false,
                            createdAt = "",
                            senderType = "system",
                            isGlobal = false,
                        )
                        notificationRepository.insert(userNotification)
                    }
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

            when (val result = notificationRepository.deleteSystemAnnouncement(notification)) {
                is Result.Success -> loadAnnouncements()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
