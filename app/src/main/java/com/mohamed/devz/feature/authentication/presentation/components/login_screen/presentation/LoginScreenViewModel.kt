package com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class LoginViewModel @Inject constructor(
    // TODO: inject AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // TODO: authRepository.login(email, password)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}