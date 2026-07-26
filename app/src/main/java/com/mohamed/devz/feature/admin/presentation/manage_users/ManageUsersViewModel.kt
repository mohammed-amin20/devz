package com.mohamed.devz.feature.admin.presentation.manage_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageUsersViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageUsersState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUsers()
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
            is ManageUsersAction.Refresh -> loadUsers()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = accountRepository.getAll()) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }
                    _uiState.update {
                        it.copy(allAccounts = sorted)
                    }
                    applyFilters()
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
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
}
