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
            is JobDetailAction.EmailChanged -> _uiState.update {
                recomputeValidation(it.copy(email = action.value, emailTouched = true))
            }
            is JobDetailAction.WhatsAppChanged -> _uiState.update {
                recomputeValidation(it.copy(whatsapp = action.value, whatsappTouched = true))
            }
            is JobDetailAction.CoverLetterChanged -> _uiState.update {
                recomputeValidation(it.copy(coverLetter = action.value, coverLetterTouched = true))
            }
            is JobDetailAction.FieldFocusChanged -> {
                if (!action.isFocused) {
                    _uiState.update {
                        recomputeValidation(it).copy(
                            emailTouched = it.emailTouched || action.field == JobApplicationField.EMAIL,
                            whatsappTouched = it.whatsappTouched || action.field == JobApplicationField.WHATSAPP,
                            coverLetterTouched = it.coverLetterTouched || action.field == JobApplicationField.COVER_LETTER,
                        )
                    }
                }
            }
            is JobDetailAction.SubmitApplication -> submitApplication(action.onSuccess)
            is JobDetailAction.DismissError -> _uiState.update { it.copy(error = null) }
            is JobDetailAction.ShowReport -> _uiState.update { it.copy(reportTarget = action.target) }
            is JobDetailAction.DismissReport -> _uiState.update { it.copy(reportTarget = null) }
        }
    }

    private fun validateEmail(value: String): UiText? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> UiText.DynamicString("Email is required")
            !EMAIL_REGEX.matches(trimmed) -> UiText.DynamicString("Invalid email format")
            else -> null
        }
    }

    private fun validateWhatsApp(value: String): UiText? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> UiText.DynamicString("WhatsApp number is required")
            !WHATSAPP_REGEX.matches(trimmed) -> UiText.DynamicString("Enter a valid WhatsApp number (8-15 digits, optional +)")
            else -> null
        }
    }

    private fun validateCoverLetter(value: String): UiText? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> UiText.DynamicString("Cover letter is required")
            trimmed.length < MIN_COVER_LETTER_LENGTH -> UiText.DynamicString("Cover letter must be at least 20 characters")
            else -> null
        }
    }

    private fun recomputeValidation(state: JobDetailState): JobDetailState {
        val emailError = validateEmail(state.email)
        val whatsappError = validateWhatsApp(state.whatsapp)
        val coverLetterError = validateCoverLetter(state.coverLetter)
        return state.copy(
            emailError = emailError,
            whatsappError = whatsappError,
            coverLetterError = coverLetterError,
            canSubmit = emailError == null && whatsappError == null && coverLetterError == null,
        )
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
                            currentAccountId = currentAccountId,
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
        _uiState.update {
            recomputeValidation(it).copy(
                showApplySheet = true,
                applicationSuccess = false,
                emailTouched = false,
                whatsappTouched = false,
                coverLetterTouched = false,
            )
        }
    }

    fun hideApplySheet() {
        _uiState.update { it.copy(showApplySheet = false) }
    }

    private fun submitApplication(onSuccess: () -> Unit) {
        val state = _uiState.value
        val jobId = state.job?.id ?: return
        val emailError = validateEmail(state.email)
        val whatsappError = validateWhatsApp(state.whatsapp)
        val coverLetterError = validateCoverLetter(state.coverLetter)
        if (emailError != null || whatsappError != null || coverLetterError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    whatsappError = whatsappError,
                    coverLetterError = coverLetterError,
                    emailTouched = true,
                    whatsappTouched = true,
                    coverLetterTouched = true,
                    canSubmit = false,
                )
            }
            return
        }
        val coverLetter = state.coverLetter.trim()

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
                            emailError = null,
                            whatsappError = null,
                            coverLetterError = null,
                            emailTouched = false,
                            whatsappTouched = false,
                            coverLetterTouched = false,
                            canSubmit = false,
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

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val WHATSAPP_REGEX = Regex("^\\+?[0-9]{8,15}$")
        private const val MIN_COVER_LETTER_LENGTH = 20
    }
}
