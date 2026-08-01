package com.mohamed.devz.feature.company.presentation.company_job_detail

import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ProposalUiModel(
    val applicationId: Int,
    val applicantId: Int,
    val fullName: String,
    val username: String,
    val avatarUrl: String,
    val email: String,
    val whatsapp: String = "",
    val points: Int,
    val status: String,
    val coverLetter: String,
    val createdAt: String = "",
    val isPro: Boolean = false,
)

data class CompanyJobDetailState(
    val job: JobPosting? = null,
    val proposals: List<ProposalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isApproving: Boolean = false,
    val error: UiText? = null,
)
