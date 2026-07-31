package com.mohamed.devz.feature.admin.presentation.manage_users

import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ManageUsersState(
    val allAccounts: List<Account> = emptyList(),
    val filteredAccounts: List<Account> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: Int = 0,
    val showBanDialog: Boolean = false,
    val targetBanAccount: Account? = null,
    val showProDialog: Boolean = false,
    val targetProAccount: Account? = null,
    val showAdminDialog: Boolean = false,
    val targetAdminAccount: Account? = null,
    val currentAccountIsMainAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
)
