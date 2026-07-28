package com.mohamed.devz.feature.company.presentation.company_dashboard

sealed interface CompanyDashboardAction {
    data object Refresh : CompanyDashboardAction
}
