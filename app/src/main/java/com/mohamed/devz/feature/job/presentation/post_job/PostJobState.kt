package com.mohamed.devz.feature.job.presentation.post_job

import com.mohamed.devz.feature.core.presentation.util.UiText

data class PostJobState(
    val title: String = "",
    val description: String = "",
    val salaryRange: String = "",
    val jobType: String = "full-time",
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
