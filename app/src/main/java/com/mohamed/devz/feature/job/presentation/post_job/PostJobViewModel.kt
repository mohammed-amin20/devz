package com.mohamed.devz.feature.job.presentation.post_job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.JobPosting
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
class PostJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostJobState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: PostJobAction) {
        when (action) {
            is PostJobAction.TitleChanged -> _uiState.update { it.copy(title = action.value) }
            is PostJobAction.DescriptionChanged -> _uiState.update { it.copy(description = action.value) }
            is PostJobAction.SalaryRangeChanged -> _uiState.update { it.copy(salaryRange = action.value) }
            is PostJobAction.JobTypeSelected -> _uiState.update { it.copy(jobType = action.value) }
            is PostJobAction.Submit -> submit(action.onSuccess)
        }
    }

    private fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = UiText.DynamicString("Title is required")) }
            return
        }
        if (state.description.isBlank()) {
            _uiState.update { it.copy(error = UiText.DynamicString("Description is required")) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("Not logged in")) }
                return@launch
            }

            val profileResult = companyProfileRepository.getByAccountId(accountId)
            val companyName = (profileResult as? Result.Success)?.data?.companyName ?: ""

            val posting = JobPosting(
                companyName = companyName,
                title = state.title,
                description = state.description,
                salaryRange = state.salaryRange,
                jobType = state.jobType,
                status = "pending",
                accountId = accountId,
            )

            when (jobRepository.insertJobPosting(posting)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("Failed to post job")) }
                }
            }
        }
    }
}
