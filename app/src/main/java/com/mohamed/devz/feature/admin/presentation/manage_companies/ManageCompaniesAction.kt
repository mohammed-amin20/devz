package com.mohamed.devz.feature.admin.presentation.manage_companies

sealed interface ManageCompaniesAction {
    data object Load : ManageCompaniesAction
    data object Refresh : ManageCompaniesAction
    data class ToggleSubscription(val profileId: Int, val activate: Boolean) : ManageCompaniesAction
}
