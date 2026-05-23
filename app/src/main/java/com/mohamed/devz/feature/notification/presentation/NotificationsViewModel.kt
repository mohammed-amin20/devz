package com.mohamed.devz.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
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

sealed interface NotificationsAction {
    data object MarkAllRead : NotificationsAction
    data class MarkRead(val id: String) : NotificationsAction
    data object Refresh : NotificationsAction
}

enum class NotificationType {
    ACCEPTED,
    UPVOTE,
    LIKE,
    COMMENT
}

data class NotificationUiModel(
    val id: String,
    val type: NotificationType,
    val actorName: String?,
    val message: String,
    val questionTitle: String,
    val timeAgo: String,
    val isRead: Boolean = false,
)

data class NotificationsUiState(
    val notifications: List<NotificationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun onAction(action: NotificationsAction) {
        when (action) {
            is NotificationsAction.MarkAllRead -> markAllRead()
            is NotificationsAction.MarkRead -> markRead(action.id)
            is NotificationsAction.Refresh -> loadNotifications()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("User not found")) }
                return@launch
            }

            when (val result = notificationRepository.getAllByAccountId(accountId)) {
                is Result.Success -> {
                    val uiModels = result.data.map { notification ->
                        NotificationUiModel(
                            id = notification.id.toString(),
                            type = mapTypeString(notification.type),
                            actorName = notification.actorName,
                            message = notification.description,
                            questionTitle = "",
                            timeAgo = notification.createdAt,
                            isRead = notification.seen,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            notifications = uiModels,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }

    private fun markAllRead() {
        viewModelScope.launch {
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) return@launch
            when (val result = notificationRepository.getAllByAccountId(accountId)) {
                is Result.Success -> {
                    result.data.forEach { notification ->
                        if (!notification.seen) {
                            notificationRepository.update(notification.copy(seen = true))
                        }
                    }
                    _uiState.update { it.copy(notifications = it.notifications.map { n -> n.copy(isRead = true) }) }
                }
                is Result.Error -> { }
            }
        }
    }

    private fun markRead(id: String) {
        viewModelScope.launch {
            val intId = id.toIntOrNull() ?: return@launch
            when (val result = notificationRepository.getAllByAccountId(
                userPreferencesRepository.observeCurrentAccountId().first() ?: return@launch
            )) {
                is Result.Success -> {
                    val notification = result.data.find { it.id == intId } ?: return@launch
                    notificationRepository.update(notification.copy(seen = true))
                    _uiState.update {
                        it.copy(notifications = it.notifications.map { n ->
                            if (n.id == id) n.copy(isRead = true) else n
                        })
                    }
                }
                is Result.Error -> { }
            }
        }
    }

    private fun mapTypeString(type: String): NotificationType {
        return when (type.uppercase()) {
            "ACCEPTED" -> NotificationType.ACCEPTED
            "UPVOTE" -> NotificationType.UPVOTE
            "LIKE" -> NotificationType.LIKE
            "COMMENT" -> NotificationType.COMMENT
            else -> NotificationType.LIKE
        }
    }
}
