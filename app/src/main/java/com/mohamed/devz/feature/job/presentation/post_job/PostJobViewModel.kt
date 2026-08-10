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
            is PostJobAction.TitleChanged -> _uiState.update {
                recomputeValidation(it.copy(title = action.value, titleTouched = true))
            }
            is PostJobAction.DescriptionChanged -> _uiState.update {
                recomputeValidation(it.copy(description = action.value, descriptionTouched = true))
            }
            is PostJobAction.SalaryRangeChanged -> _uiState.update { it.copy(salaryRange = action.value) }
            is PostJobAction.JobTypeSelected -> _uiState.update { it.copy(jobType = action.value) }
            is PostJobAction.FieldFocusChanged -> {
                if (!action.isFocused) {
                    _uiState.update {
                        recomputeValidation(it).copy(
                            titleTouched = it.titleTouched || action.field == PostJobField.TITLE,
                            descriptionTouched = it.descriptionTouched || action.field == PostJobField.DESCRIPTION,
                        )
                    }
                }
            }
            is PostJobAction.Submit -> submit(action.onSuccess)
        }
    }

    private fun validateTitle(value: String): UiText? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> UiText.DynamicString("Title is required")
            trimmed.length < MIN_TITLE_LENGTH -> UiText.DynamicString("Title must be at least 3 characters")
            else -> null
        }
    }

    private fun validateDescription(value: String): UiText? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> UiText.DynamicString("Description is required")
            trimmed.length < MIN_DESCRIPTION_LENGTH -> UiText.DynamicString("Description must be at least 20 characters")
            else -> null
        }
    }

    private fun recomputeValidation(state: PostJobState): PostJobState {
        val titleError = validateTitle(state.title)
        val descriptionError = validateDescription(state.description)
        return state.copy(
            titleError = titleError,
            descriptionError = descriptionError,
            canSubmit = titleError == null && descriptionError == null,
        )
    }

    private fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        val titleError = validateTitle(state.title)
        val descriptionError = validateDescription(state.description)
        if (titleError != null || descriptionError != null) {
            _uiState.update {
                it.copy(
                    titleError = titleError,
                    descriptionError = descriptionError,
                    titleTouched = true,
                    descriptionTouched = true,
                    canSubmit = false,
                )
            }
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

            val result = jobRepository.insertJobPosting(posting)
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.toUIText()) }
                }
            }
        }
    }

    companion object {
        private const val MIN_TITLE_LENGTH = 3
        private const val MIN_DESCRIPTION_LENGTH = 20
    }
}
