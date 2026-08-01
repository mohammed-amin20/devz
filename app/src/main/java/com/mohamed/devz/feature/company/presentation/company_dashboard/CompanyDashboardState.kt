package com.mohamed.devz.feature.company.presentation.company_dashboard

import com.mohamed.devz.feature.core.presentation.util.UiText

data class CompanyJobUiModel(
    val id: Int,
    val title: String,
    val status: String,
    val createdAt: String = "",
)

data class CompanyDashboardState(
    val companyName: String = "",
    val logoUrl: String = "",
    val website: String = "",
    val description: String = "",
    val subscriptionStatus: String = "pending",
    val offeredJobs: List<CompanyJobUiModel> = emptyList(),
    val reservedJobs: List<CompanyJobUiModel> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
)
