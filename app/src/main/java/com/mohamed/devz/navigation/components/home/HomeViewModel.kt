package com.mohamed.devz.navigation.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.FcmTokenUtil
import com.mohamed.devz.feature.core.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationRepository: NotificationRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _currentAccountId = MutableStateFlow(0)
    val currentAccountId = _currentAccountId.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    init {
        viewModelScope.launch {
            val id = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            _currentAccountId.value = id
            loadUnreadCount(id)
            if (id != 0) {
                FcmTokenUtil.saveCurrentToken(accountRepository, userPreferencesRepository)
            }
        }
    }

    fun onSelectedIndexChange(index: Int) {
        _selectedIndex.update { index }
        if (index != 2) {
            viewModelScope.launch {
                loadUnreadCount(_currentAccountId.value)
            }
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            loadUnreadCount(_currentAccountId.value)
        }
    }

    private suspend fun loadUnreadCount(accountId: Int) {
        if (accountId == 0) return
        when (val result = notificationRepository.getAllByAccountId(accountId)) {
            is Result.Success -> {
                _unreadCount.value = result.data.count { !it.isRead }
            }
            is Result.Error -> { }
        }
    }
}
