package com.mohamed.devz.feature.job.presentation.job_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.JobApplication
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
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
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: JobDetailAction) {
        when (action) {
            is JobDetailAction.LoadJob -> loadJob(action.jobId)
            is JobDetailAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is JobDetailAction.WhatsAppChanged -> _uiState.update { it.copy(whatsapp = action.value) }
            is JobDetailAction.CoverLetterChanged -> _uiState.update { it.copy(coverLetter = action.value) }
            is JobDetailAction.SubmitApplication -> submitApplication(action.onSuccess)
            is JobDetailAction.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loadJob(jobId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = jobRepository.getJobPostingById(jobId)) {
                is Result.Success -> {
                    val job = result.data
                    val appsResult = jobRepository.getApplicationsByJobId(jobId)
                    val applicantCount = (appsResult as? Result.Success)?.data?.size ?: 0
                    val profileResult = companyProfileRepository.getByAccountId(job.accountId)
                    val logoUrl = (profileResult as? Result.Success)?.data?.logoUrl ?: ""
                    val currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
                    val myAppsResult = if (currentAccountId == 0) null else jobRepository.getApplicationsByApplicantId(currentAccountId)
                    val hasApplied = (myAppsResult as? Result.Success)?.data?.any { it.jobId == jobId } ?: false
                    _uiState.update {
                        it.copy(
                            job = JobDetailUiModel(
                                id = job.id,
                                accountId = job.accountId,
                                companyName = job.companyName,
                                logoUrl = logoUrl,
                                title = job.title,
                                description = job.description,
                                salaryRange = job.salaryRange,
                                jobType = job.jobType,
                                createdAt = job.createdAt,
                                status = job.status,
                                applicantCount = applicantCount,
                            ),
                            hasApplied = hasApplied,
                            isLoading = false,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.error.toUIText(), isLoading = false)
                    }
                }
            }
        }
    }

    fun showApplySheet() {
        _uiState.update { it.copy(showApplySheet = true, applicationSuccess = false) }
    }

    fun hideApplySheet() {
        _uiState.update { it.copy(showApplySheet = false) }
    }

    private fun submitApplication(onSuccess: () -> Unit) {
        val state = _uiState.value
        val jobId = state.job?.id ?: return
        val coverLetter = state.coverLetter.trim()
        if (coverLetter.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            val currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (currentAccountId == 0) {
                _uiState.update {
                    it.copy(
                        error = UiText.DynamicString("Not logged in. Please sign in to apply."),
                        isSubmitting = false,
                    )
                }
                return@launch
            }

            val myAppsResult = jobRepository.getApplicationsByApplicantId(currentAccountId)
            val alreadyApplied = (myAppsResult as? Result.Success)?.data?.any { it.jobId == jobId } ?: false
            if (alreadyApplied) {
                _uiState.update {
                    it.copy(
                        error = UiText.DynamicString("You have already applied for this position."),
                        hasApplied = true,
                        isSubmitting = false,
                    )
                }
                return@launch
            }

            val application = JobApplication(
                id = 0,
                jobId = jobId,
                applicantId = currentAccountId,
                coverLetter = coverLetter,
                status = "pending",
                createdAt = "",
                email = state.email.trim(),
                whatsapp = state.whatsapp.trim(),
            )

            when (val result = jobRepository.insertApplication(application)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            showApplySheet = false,
                            applicationSuccess = true,
                            hasApplied = true,
                            email = "",
                            whatsapp = "",
                            coverLetter = "",
                        )
                    }
                    onSuccess()
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.error.toUIText(), isSubmitting = false)
                    }
                }
            }
        }
    }
}
