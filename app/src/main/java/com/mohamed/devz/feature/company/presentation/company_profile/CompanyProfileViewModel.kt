package com.mohamed.devz.feature.company.presentation.company_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyJobUiModel
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
class CompanyProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val companyProfileRepository: CompanyProfileRepository,
    private val jobRepository: JobRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var targetAccountId: Int? = null

    init {
        targetAccountId = savedStateHandle.get<Int>("accountId")
        load(targetAccountId)
    }

    fun onAction(action: CompanyProfileAction) {
        when (action) {
            CompanyProfileAction.Refresh -> load(targetAccountId)
            is CompanyProfileAction.SelectTab -> {
                _uiState.update { it.copy(selectedTab = action.index) }
            }
            is CompanyProfileAction.SetTargetAccountId -> {
                targetAccountId = action.accountId
                load(targetAccountId)
            }
        }
    }

    private fun load(targetAccountId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val loggedInAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            val accountId = targetAccountId ?: loggedInAccountId
            if (accountId == 0) {
                _uiState.update {
                    it.copy(isLoading = false, error = UiText.DynamicString("Company profile not found"))
                }
                return@launch
            }

            when (val profileResult = companyProfileRepository.getByAccountId(accountId)) {
                is Result.Success -> {
                    val profile = profileResult.data
                    if (profile != null) {
                        val jobsResult = jobRepository.getJobPostingsByAccountId(accountId)
                        val jobs = (jobsResult as? Result.Success)?.data ?: emptyList()

                        val postedJobs = jobs.filter { j -> j.status == "approved" || j.status == "active" }
                        val filledJobs = jobs.filter { j -> j.status == "filled" }

                        var totalApplicants = 0
                        for (job in jobs) {
                            val appsResult = jobRepository.getApplicationsByJobId(job.id)
                            totalApplicants += (appsResult as? Result.Success)?.data?.size ?: 0
                        }

                        _uiState.update {
                            it.copy(
                                companyName = profile.companyName,
                                logoUrl = profile.logoUrl,
                                website = profile.website,
                                description = profile.description,
                                bio = profile.bio,
                                location = profile.location,
                                industry = profile.industry,
                                twitterUrl = profile.twitterUrl,
                                rating = profile.rating,
                                isVerified = profile.isVerified,
                                isOwnProfile = accountId == loggedInAccountId,
                                postedJobs = postedJobs.map { j ->
                                    CompanyJobUiModel(id = j.id, title = j.title, status = j.status, createdAt = j.createdAt)
                                },
                                filledJobs = filledJobs.map { j ->
                                    CompanyJobUiModel(id = j.id, title = j.title, status = j.status, createdAt = j.createdAt)
                                },
                                totalJobs = jobs.size,
                                totalApplicants = totalApplicants,
                                isLoading = false,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = UiText.DynamicString("Company profile not found"))
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = profileResult.error.toUIText()) }
                }
            }
        }
    }
}
