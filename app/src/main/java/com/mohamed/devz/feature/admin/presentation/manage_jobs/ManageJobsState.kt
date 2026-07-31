package com.mohamed.devz.feature.admin.presentation.manage_jobs

import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ManageJobsState(
    val jobs: List<JobPosting> = emptyList(),
    val companyLogos: Map<Int, String> = emptyMap(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val showConfirmDialog: Boolean = false,
    val targetJob: JobPosting? = null,
    val isApproving: Boolean = false,
)
