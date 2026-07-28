package com.mohamed.devz.feature.job.presentation.job_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.JobApplication
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
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
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: JobDetailAction) {
        when (action) {
            is JobDetailAction.LoadJob -> loadJob(action.jobId)
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
                    _uiState.update {
                        it.copy(
                            job = JobDetailUiModel(
                                id = job.id,
                                companyName = job.companyName,
                                title = job.title,
                                description = job.description,
                                salaryRange = job.salaryRange,
                                jobType = job.jobType,
                                createdAt = job.createdAt,
                            ),
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

            val application = JobApplication(
                id = 0,
                jobId = jobId,
                applicantId = currentAccountId,
                coverLetter = coverLetter,
                status = "pending",
                createdAt = "",
            )

            when (val result = jobRepository.insertApplication(application)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            showApplySheet = false,
                            applicationSuccess = true,
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
