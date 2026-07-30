package com.mohamed.devz.feature.company.presentation.company_job_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
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
class CompanyJobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val accountRepository: AccountRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyJobDetailState())
    val uiState = _uiState.asStateFlow()

    private var jobTitle: String = ""

    fun onAction(action: CompanyJobDetailAction) {
        when (action) {
            is CompanyJobDetailAction.LoadJob -> loadJob(action.jobId)
            is CompanyJobDetailAction.ApproveApplication -> approveApplication(
                action.applicationId,
                action.applicantId,
            )
        }
    }

    private fun loadJob(jobId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val jobResult = jobRepository.getJobPostingById(jobId)) {
                is Result.Success -> {
                    val job = jobResult.data
                    jobTitle = job.title
                    when (val appsResult = jobRepository.getApplicationsByJobId(jobId)) {
                        is Result.Success -> {
                            val proposals = buildProposals(appsResult.data)
                            _uiState.update {
                                it.copy(
                                    job = job,
                                    proposals = proposals,
                                    isLoading = false,
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    job = job,
                                    proposals = emptyList(),
                                    isLoading = false,
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = jobResult.error.toUIText()) }
                }
            }
        }
    }

    private suspend fun buildProposals(applications: List<com.mohamed.devz.feature.core.domain.model.JobApplication>): List<ProposalUiModel> {
        val result = mutableListOf<ProposalUiModel>()
        for (app in applications) {
            val accountResult = accountRepository.getById(app.applicantId)
            val account = (accountResult as? Result.Success)?.data
            result.add(
                ProposalUiModel(
                    applicationId = app.id,
                    applicantId = app.applicantId,
                    fullName = account?.fullName ?: "Unknown",
                    username = account?.username ?: "",
                    avatarUrl = account?.imageUrl ?: "",
                    email = app.email.ifBlank { account?.email ?: "" },
                    whatsapp = app.whatsapp.ifBlank { account?.phoneNumber ?: "" },
                    points = account?.points ?: 0,
                    status = app.status,
                    coverLetter = app.coverLetter,
                    createdAt = app.createdAt,
                )
            )
        }
        return result
    }

    private fun approveApplication(applicationId: Int, applicantId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isApproving = true) }
            when (jobRepository.updateApplicationStatus(applicationId, "reserved")) {
                is Result.Success -> {
                    val currentJob = _uiState.value.job
                    if (currentJob != null) {
                        jobRepository.updateJobPosting(currentJob.copy(status = "filled"))
                    }
                    val jobId = _uiState.value.job?.id ?: return@launch
                    val accountResult = accountRepository.getById(applicantId)
                    val account = (accountResult as? Result.Success)?.data
                    if (account != null && account.fcmToken.isNotBlank()) {
                        fcmPushSender.sendPush(
                            fcmToken = account.fcmToken,
                            title = "Application Accepted",
                            body = "Your application for $jobTitle has been accepted!",
                            questionId = null,
                            type = "job",
                            actorId = null,
                            jobId = jobId,
                        )
                    }
                    loadJob(jobId)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isApproving = false,
                            error = UiText.DynamicString("Failed to approve application"),
                        )
                    }
                }
            }
        }
    }
}
