package com.mohamed.devz.feature.company.presentation.company_profile

sealed class CompanyProfileAction {
    data object Refresh : CompanyProfileAction()
    data class SelectTab(val index: Int) : CompanyProfileAction()
    data class SetTargetAccountId(val accountId: Int?) : CompanyProfileAction()
}
