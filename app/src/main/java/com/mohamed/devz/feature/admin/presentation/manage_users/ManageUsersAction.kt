package com.mohamed.devz.feature.admin.presentation.manage_users

import com.mohamed.devz.feature.core.domain.model.Account

sealed interface ManageUsersAction {
    data class SearchQueryChanged(val value: String) : ManageUsersAction
    data object FilterAll : ManageUsersAction
    data object FilterBanned : ManageUsersAction
    data object FilterPro : ManageUsersAction
    data object FilterAdmin : ManageUsersAction
    data class BanUser(val account: Account) : ManageUsersAction
    data class ConfirmBan(val account: Account) : ManageUsersAction
    data object DismissBanDialog : ManageUsersAction
    data class UnbanUser(val account: Account) : ManageUsersAction
    data class ShowProDialog(val account: Account) : ManageUsersAction
    data class ConfirmPro(val account: Account) : ManageUsersAction
    data object DismissProDialog : ManageUsersAction
    data class ShowAdminDialog(val account: Account) : ManageUsersAction
    data class ConfirmAdmin(val account: Account) : ManageUsersAction
    data object DismissAdminDialog : ManageUsersAction
    data object Refresh : ManageUsersAction
}
