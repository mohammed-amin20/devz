package com.mohamed.devz.feature.admin.presentation.manage_companies

import com.mohamed.devz.feature.core.presentation.util.UiText

data class CompanyUiModel(
    val id: Int,
    val companyName: String,
    val logoUrl: String = "",
    val website: String,
    val subscriptionStatus: String,
    val accountId: Int,
)

data class ManageCompaniesState(
    val companies: List<CompanyUiModel> = emptyList(),
    val filteredCompanies: List<CompanyUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
)
