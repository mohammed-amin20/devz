package com.mohamed.devz.feature.company.presentation.company_profile

import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyJobUiModel
import com.mohamed.devz.feature.core.presentation.util.UiText

data class CompanyProfileUiState(
    val companyName: String = "",
    val logoUrl: String = "",
    val website: String = "",
    val description: String = "",
    val bio: String = "",
    val location: String = "",
    val industry: String = "",
    val twitterUrl: String = "",
    val rating: Double = 0.0,
    val isVerified: Boolean = false,
    val isOwnProfile: Boolean = false,
    val postedJobs: List<CompanyJobUiModel> = emptyList(),
    val filledJobs: List<CompanyJobUiModel> = emptyList(),
    val totalJobs: Int = 0,
    val totalApplicants: Int = 0,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val error: UiText? = null,
)
