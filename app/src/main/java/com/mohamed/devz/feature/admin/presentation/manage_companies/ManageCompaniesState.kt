package com.mohamed.devz.feature.admin.presentation.manage_companies

import com.mohamed.devz.feature.core.presentation.util.UiText

data class CompanyUiModel(
    val id: Int,
    val companyName: String,
    val website: String,
    val subscriptionStatus: String,
    val accountId: Int,
)

data class ManageCompaniesState(
    val companies: List<CompanyUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiText? = null,
)
