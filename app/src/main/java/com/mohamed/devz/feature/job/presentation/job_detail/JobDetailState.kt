package com.mohamed.devz.feature.job.presentation.job_detail

import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.report.presentation.ReportTarget

data class JobDetailState(
    val job: JobDetailUiModel? = null,
    val email: String = "",
    val whatsapp: String = "",
    val coverLetter: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val showApplySheet: Boolean = false,
    val error: UiText? = null,
    val applicationSuccess: Boolean = false,
    val hasApplied: Boolean = false,
    val currentAccountId: Int = 0,
    val reportTarget: ReportTarget? = null,
)

data class JobDetailUiModel(
    val id: Int,
    val accountId: Int,
    val companyName: String,
    val logoUrl: String = "",
    val title: String,
    val description: String,
    val salaryRange: String,
    val jobType: String,
    val createdAt: String,
    val status: String = "approved",
    val applicantCount: Int = 0,
)
