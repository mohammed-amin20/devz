package com.mohamed.devz.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime
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
    ANSWER
}

data class NotificationUiModel(
    val id: String,
    val type: NotificationType,
    val actorName: String?,
    val message: String,
    val questionId: Int,
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
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    private var rawNotifications: Map<Int, DomainNotification> = emptyMap()

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
                    val notifications = result.data
                    val questionIds = notifications.map { it.questionId }.distinct()
                    val questionTitles = questionIds.associateWith { qId ->
                        when (val qResult = questionRepository.getById(qId)) {
                            is Result.Success -> qResult.data.title
                            is Result.Error -> ""
                        }
                    }
                    rawNotifications = notifications.associateBy { it.id }
                    val uiModels = notifications.map { notification ->
                        NotificationUiModel(
                            id = notification.id.toString(),
                            type = mapTypeString(notification.type),
                            actorName = notification.actorName,
                            message = notification.message,
                            questionId = notification.questionId,
                            questionTitle = questionTitles[notification.questionId] ?: "",
                            timeAgo = formatRelativeTime(notification.createdAt),
                            isRead = notification.isRead,
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
            val unreadIds = rawNotifications.filter { !it.value.isRead }.keys
            if (unreadIds.isEmpty()) return@launch

            unreadIds.forEach { id ->
                val raw = rawNotifications[id] ?: return@forEach
                notificationRepository.update(raw.copy(isRead = true))
            }
            rawNotifications = rawNotifications.mapValues { it.value.copy(isRead = true) }
            _uiState.update { it.copy(notifications = it.notifications.map { n -> n.copy(isRead = true) }) }
        }
    }

    private fun markRead(id: String) {
        viewModelScope.launch {
            val intId = id.toIntOrNull() ?: return@launch
            val raw = rawNotifications[intId] ?: return@launch
            notificationRepository.update(raw.copy(isRead = true))
            rawNotifications = rawNotifications + (intId to raw.copy(isRead = true))
            _uiState.update {
                it.copy(notifications = it.notifications.map { n ->
                    if (n.id == id) n.copy(isRead = true) else n
                })
            }
        }
    }

    private fun mapTypeString(type: String): NotificationType {
        return when (type.lowercase()) {
            "accepted" -> NotificationType.ACCEPTED
            "upvote" -> NotificationType.UPVOTE
            "like" -> NotificationType.LIKE
            "answer" -> NotificationType.ANSWER
            else -> NotificationType.LIKE
        }
    }
}
