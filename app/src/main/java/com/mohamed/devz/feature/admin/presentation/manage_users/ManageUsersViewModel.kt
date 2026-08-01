package com.mohamed.devz.feature.admin.presentation.manage_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageUsersViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageUsersState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentAdmin()
    }

    fun onAction(action: ManageUsersAction) {
        when (action) {
            is ManageUsersAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
                applyFilters()
            }
            is ManageUsersAction.FilterAll -> {
                _uiState.update { it.copy(selectedFilter = 0) }
                applyFilters()
            }
            is ManageUsersAction.FilterBanned -> {
                _uiState.update { it.copy(selectedFilter = 1) }
                applyFilters()
            }
            is ManageUsersAction.FilterPro -> {
                _uiState.update { it.copy(selectedFilter = 2) }
                applyFilters()
            }
            is ManageUsersAction.FilterAdmin -> {
                _uiState.update { it.copy(selectedFilter = 3) }
                applyFilters()
            }
            is ManageUsersAction.BanUser -> {
                _uiState.update {
                    it.copy(showBanDialog = true, targetBanAccount = action.account)
                }
            }
            is ManageUsersAction.ConfirmBan -> {
                banUser(action.account)
            }
            is ManageUsersAction.DismissBanDialog -> {
                _uiState.update {
                    it.copy(showBanDialog = false, targetBanAccount = null)
                }
            }
            is ManageUsersAction.UnbanUser -> {
                unbanUser(action.account)
            }
            is ManageUsersAction.ShowProDialog -> {
                _uiState.update {
                    it.copy(showProDialog = true, targetProAccount = action.account)
                }
            }
            is ManageUsersAction.ConfirmPro -> {
                togglePro(action.account)
            }
            is ManageUsersAction.DismissProDialog -> {
                _uiState.update {
                    it.copy(showProDialog = false, targetProAccount = null)
                }
            }
            is ManageUsersAction.ShowAdminDialog -> {
                _uiState.update {
                    it.copy(showAdminDialog = true, targetAdminAccount = action.account)
                }
            }
            is ManageUsersAction.ConfirmAdmin -> {
                toggleAdmin(action.account)
            }
            is ManageUsersAction.DismissAdminDialog -> {
                _uiState.update {
                    it.copy(showAdminDialog = false, targetAdminAccount = null)
                }
            }
            is ManageUsersAction.Refresh -> loadUsers(isRefresh = true)
        }
    }

    private fun loadCurrentAdmin() {
        viewModelScope.launch {
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                loadUsers()
                return@launch
            }
            when (val result = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(currentAccountIsMainAdmin = result.data.isMainAdmin)
                    }
                }
                is Result.Error -> { }
            }
            loadUsers()
        }
    }

    private fun loadUsers(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }

            when (val result = accountRepository.getAll()) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }
                    val withoutMainAdmins = sorted.filter { !it.isMainAdmin && it.accountType != "company" }
                    _uiState.update {
                        it.copy(allAccounts = withoutMainAdmins)
                    }
                    applyFilters()
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error.toUIText(),
                        )
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.allAccounts

        if (state.selectedFilter == 1) {
            filtered = filtered.filter { it.isBanned }
        } else if (state.selectedFilter == 2) {
            filtered = filtered.filter { it.isPro }
        } else if (state.selectedFilter == 3) {
            filtered = filtered.filter { it.isAdmin }
        }

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.username.lowercase().contains(query) ||
                        it.fullName.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredAccounts = filtered) }
    }

    private fun banUser(account: Account) {
        viewModelScope.launch {
            _uiState.update { it.copy(showBanDialog = false, targetBanAccount = null) }

            when (val result = accountRepository.update(account.copy(isBanned = true))) {
                is Result.Success -> loadUsers()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun unbanUser(account: Account) {
        viewModelScope.launch {
            when (val result = accountRepository.update(account.copy(isBanned = false))) {
                is Result.Success -> loadUsers()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun togglePro(account: Account) {
        viewModelScope.launch {
            _uiState.update { it.copy(showProDialog = false, targetProAccount = null) }

            when (val result = accountRepository.update(account.copy(isPro = !account.isPro))) {
                is Result.Success -> loadUsers()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun toggleAdmin(account: Account) {
        viewModelScope.launch {
            _uiState.update { it.copy(showAdminDialog = false, targetAdminAccount = null) }

            when (val result = accountRepository.update(account.copy(isAdmin = !account.isAdmin))) {
                is Result.Success -> loadUsers()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
