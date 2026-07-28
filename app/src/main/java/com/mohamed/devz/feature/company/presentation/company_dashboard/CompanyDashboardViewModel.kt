package com.mohamed.devz.feature.company.presentation.company_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class CompanyDashboardViewModel @Inject constructor(
    private val companyProfileRepository: CompanyProfileRepository,
    private val jobRepository: JobRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyDashboardState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onAction(action: CompanyDashboardAction) {
        when (action) {
            CompanyDashboardAction.Refresh -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("Not logged in")) }
                return@launch
            }

            when (val profileResult = companyProfileRepository.getByAccountId(accountId)) {
                is Result.Success -> {
                    val profile = profileResult.data
                    if (profile != null) {
                        val jobsResult = jobRepository.getJobPostingsByAccountId(accountId)
                        val jobs = (jobsResult as? Result.Success)?.data ?: emptyList()

                        _uiState.update {
                            it.copy(
                                companyName = profile.companyName,
                                website = profile.website,
                                description = profile.description,
                                subscriptionStatus = profile.subscriptionStatus,
                                jobPostings = jobs.map { j ->
                                    CompanyJobUiModel(id = j.id, title = j.title, status = j.status)
                                },
                                isLoading = false,
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("Company profile not found")) }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = profileResult.error.toUIText()) }
                }
            }
        }
    }
}
