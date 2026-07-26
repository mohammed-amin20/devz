package com.mohamed.devz.feature.admin.presentation.manage_announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Announcement
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnnouncementRepository
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
    private val announcementRepository: AnnouncementRepository,
    private val accountRepository: AccountRepository,
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
                        targetDeleteAnnouncement = action.announcement
                    )
                }
            }
            is ManageAnnouncementsAction.ConfirmDelete -> {
                deleteAnnouncement(action.announcement)
            }
            is ManageAnnouncementsAction.DismissDeleteDialog -> {
                _uiState.update {
                    it.copy(showDeleteDialog = false, targetDeleteAnnouncement = null)
                }
            }
            is ManageAnnouncementsAction.Refresh -> loadAnnouncements()
        }
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = announcementRepository.getAll()) {
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

            when (val result = announcementRepository.insert(
                Announcement(id = 0, title = state.createTitle, message = state.createMessage)
            )) {
                is Result.Success -> {
                    val title = state.createTitle
                    val message = state.createMessage
                    launch {
                        when (val accountsResult = accountRepository.getAll()) {
                            is Result.Success -> {
                                accountsResult.data
                                    .filter { it.fcmToken.isNotBlank() }
                                    .forEach { account ->
                                        fcmPushSender.sendPush(
                                            fcmToken = account.fcmToken,
                                            title = title,
                                            body = message,
                                            questionId = null,
                                            type = "announcement",
                                        )
                                    }
                            }
                            is Result.Error -> {}
                        }
                    }
                    loadAnnouncements()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = false, targetDeleteAnnouncement = null) }

            when (val result = announcementRepository.delete(announcement.id)) {
                is Result.Success -> loadAnnouncements()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
