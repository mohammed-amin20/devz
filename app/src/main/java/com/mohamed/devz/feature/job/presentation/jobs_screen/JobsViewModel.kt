package com.mohamed.devz.feature.job.presentation.jobs_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyProfileRepository: CompanyProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsState())
    val uiState = _uiState.asStateFlow()

    init {
        onAction(JobsAction.LoadJobs)
    }

    fun onAction(action: JobsAction) {
        when (action) {
            is JobsAction.LoadJobs -> loadJobs()
            JobsAction.Refresh -> loadJobs(isRefresh = true)
            is JobsAction.FilterByType -> filterByType(action.type)
        }
    }

    private fun loadJobs(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }

            when (val result = jobRepository.getApprovedJobPostings()) {
                is Result.Success -> {
                    val logosResult = companyProfileRepository.getAll()
                    val logoUrls = (logosResult as? Result.Success)?.data
                        ?.associate { it.userId to it.logoUrl }
                        .orEmpty()
                    val uiModels = result.data.map { job ->
                        JobListingUiModel(
                            id = job.id,
                            accountId = job.accountId,
                            companyName = job.companyName,
                            logoUrl = logoUrls[job.accountId] ?: "",
                            title = job.title,
                            salaryRange = job.salaryRange,
                            jobType = job.jobType,
                            createdAt = job.createdAt,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            jobs = uiModels,
                            filteredJobs = uiModels,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.error.toUIText(),
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
            }
        }
    }

    private fun filterByType(type: String?) {
        _uiState.update {
            it.copy(
                selectedFilter = type,
                filteredJobs = if (type == null) it.jobs
                else it.jobs.filter { job -> job.jobType == type }
            )
        }
    }
}
