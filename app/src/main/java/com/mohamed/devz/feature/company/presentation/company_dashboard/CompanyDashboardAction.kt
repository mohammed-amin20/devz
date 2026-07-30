package com.mohamed.devz.feature.company.presentation.company_dashboard

sealed interface CompanyDashboardAction {
    data object Refresh : CompanyDashboardAction
    data class SelectTab(val index: Int) : CompanyDashboardAction
    data object Logout : CompanyDashboardAction
}
