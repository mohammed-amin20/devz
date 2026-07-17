package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.FcmTokenUtil
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.FullNameChanged -> _uiState.update { it.copy(fullName = action.value, fullNameError = null) }
            is SignUpAction.UsernameChanged -> _uiState.update { it.copy(username = action.value, usernameError = null) }
            is SignUpAction.EmailChanged -> _uiState.update { it.copy(email = action.value, emailError = null) }
            is SignUpAction.PasswordChanged -> _uiState.update { it.copy(password = action.value, passwordError = null) }
            is SignUpAction.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.value, confirmPasswordError = null) }
            is SignUpAction.RegisterClicked -> register(action.onSuccess)
        }
    }

    private fun register(onSuccess: () -> Unit) {
        val state = _uiState.value

        val fullNameError = if (state.fullName.isBlank()) UiText.DynamicString("Full name is required") else null
        val usernameError = if (state.username.isBlank()) UiText.DynamicString("Username is required") else null
        val emailError = when {
            state.email.isBlank() -> UiText.DynamicString("Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> UiText.DynamicString("Invalid email format")
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> UiText.DynamicString("Password is required")
            state.password.length < 6 -> UiText.DynamicString("Password must be at least 6 characters")
            else -> null
        }
        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> UiText.DynamicString("Please confirm your password")
            state.confirmPassword != state.password -> UiText.DynamicString("Passwords do not match")
            else -> null
        }

        if (fullNameError != null || usernameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        val account = Account(
            id = 0,
            username = state.username,
            fullName = state.fullName,
            email = state.email,
            password = state.password,
            imageUrl = "",
            bio = "",
            techStack = "",
            githubUrl = "",
            linkedInUrl = "",
            websiteUrl = "",
        )
        viewModelScope.launch {
            when (val result = accountRepository.insert(account)) {
                is com.mohamed.devz.feature.core.domain.util.Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    userPreferencesRepository.setLoggedIn()
                    userPreferencesRepository.setAccountId(result.data.id)
                    FcmTokenUtil.saveCurrentToken(accountRepository, userPreferencesRepository)
                    onSuccess()
                }
                is com.mohamed.devz.feature.core.domain.util.Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }
}
