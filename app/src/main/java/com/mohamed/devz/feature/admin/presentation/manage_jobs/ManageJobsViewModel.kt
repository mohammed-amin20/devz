package com.mohamed.devz.feature.admin.presentation.manage_jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val accountRepository: AccountRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageJobsState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onAction(action: ManageJobsAction) {
        when (action) {
            ManageJobsAction.LoadJobs -> load()
            is ManageJobsAction.ApproveJob -> {
                _uiState.update { it.copy(showConfirmDialog = true, targetJob = action.job) }
            }
            is ManageJobsAction.RejectJob -> {
                rejectJob(action.job)
            }
            ManageJobsAction.DismissDialog -> {
                _uiState.update { it.copy(showConfirmDialog = false, targetJob = null) }
            }
        }
    }

    fun confirmApprove() {
        val job = _uiState.value.targetJob ?: return
        _uiState.update { it.copy(isApproving = true) }
        viewModelScope.launch {
            val updated = job.copy(status = "approved")
            when (jobRepository.updateJobPosting(updated)) {
                is Result.Success -> {
                    sendPushNotification(job.accountId, "Job Approved", "Your job \"${job.title}\" has been approved!")
                    load()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isApproving = false, error = UiText.DynamicString("Failed to approve")) }
                }
            }
        }
        _uiState.update { it.copy(showConfirmDialog = false, targetJob = null, isApproving = false) }
    }

    private fun rejectJob(job: JobPosting) {
        viewModelScope.launch {
            val updated = job.copy(status = "rejected")
            when (jobRepository.updateJobPosting(updated)) {
                is Result.Success -> {
                    sendPushNotification(job.accountId, "Job Rejected", "Your job \"${job.title}\" has been rejected.")
                    load()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = UiText.DynamicString("Failed to reject")) }
                }
            }
        }
    }

    private suspend fun sendPushNotification(accountId: Int, title: String, body: String) {
        val accountResult = accountRepository.getById(accountId)
        val account = (accountResult as? Result.Success)?.data ?: return
        if (account.fcmToken.isNotBlank()) {
            fcmPushSender.sendPush(
                fcmToken = account.fcmToken,
                title = title,
                body = body,
                questionId = null,
                type = "job",
                actorId = null,
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = jobRepository.getAllJobPostings()) {
                is Result.Success -> {
                    _uiState.update { it.copy(jobs = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }
}
