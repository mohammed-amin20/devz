package com.mohamed.devz.feature.company.presentation.company_dashboard

import com.mohamed.devz.feature.core.presentation.util.UiText

data class CompanyJobUiModel(
    val id: Int,
    val title: String,
    val status: String,
)

data class CompanyDashboardState(
    val companyName: String = "",
    val website: String = "",
    val description: String = "",
    val subscriptionStatus: String = "pending",
    val jobPostings: List<CompanyJobUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiText? = null,
)
