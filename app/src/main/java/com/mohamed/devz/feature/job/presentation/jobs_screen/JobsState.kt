package com.mohamed.devz.feature.job.presentation.jobs_screen

import com.mohamed.devz.feature.core.presentation.util.UiText

data class JobsState(
    val jobs: List<JobListingUiModel> = emptyList(),
    val filteredJobs: List<JobListingUiModel> = emptyList(),
    val selectedFilter: String? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
)

data class JobListingUiModel(
    val id: Int,
    val companyName: String,
    val logoUrl: String = "",
    val title: String,
    val salaryRange: String,
    val jobType: String,
    val createdAt: String,
)
