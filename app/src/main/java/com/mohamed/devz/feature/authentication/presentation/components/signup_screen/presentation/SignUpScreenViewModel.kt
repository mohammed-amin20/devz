package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    // TODO: inject AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpScreenState())
    val uiState = _uiState.asStateFlow()

    fun onFullNameChange(v: String)        = _uiState.update { it.copy(fullName = v) }
    fun onUsernameChange(v: String)        = _uiState.update { it.copy(username = v) }
    fun onEmailChange(v: String)           = _uiState.update { it.copy(email = v) }
    fun onPasswordChange(v: String)        = _uiState.update { it.copy(password = v) }
    fun onConfirmPasswordChange(v: String) = _uiState.update { it.copy(confirmPassword = v) }

    fun register(onSuccess: () -> Unit) {
        if (uiState.value.password != uiState.value.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // TODO: authRepository.register(...)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}