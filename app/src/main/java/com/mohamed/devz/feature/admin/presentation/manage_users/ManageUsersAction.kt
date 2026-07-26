package com.mohamed.devz.feature.admin.presentation.manage_users

import com.mohamed.devz.feature.core.domain.model.Account

sealed interface ManageUsersAction {
    data class SearchQueryChanged(val value: String) : ManageUsersAction
    data object FilterAll : ManageUsersAction
    data object FilterBanned : ManageUsersAction
    data class BanUser(val account: Account) : ManageUsersAction
    data class ConfirmBan(val account: Account) : ManageUsersAction
    data object DismissBanDialog : ManageUsersAction
    data class UnbanUser(val account: Account) : ManageUsersAction
    data object Refresh : ManageUsersAction
}
