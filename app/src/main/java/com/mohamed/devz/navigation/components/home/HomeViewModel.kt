package com.mohamed.devz.navigation.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
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
) : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _currentAccountId = MutableStateFlow(0)
    val currentAccountId = _currentAccountId.asStateFlow()

    init {
        viewModelScope.launch {
            _currentAccountId.value = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
        }
    }

    fun onSelectedIndexChange(index: Int) {
        _selectedIndex.update { index }
    }
}
