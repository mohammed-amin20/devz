package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

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
            is SignUpAction.FullNameChanged -> _uiState.update { it.copy(fullName = action.value) }
            is SignUpAction.UsernameChanged -> _uiState.update { it.copy(username = action.value) }
            is SignUpAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is SignUpAction.PasswordChanged -> _uiState.update { it.copy(password = action.value) }
            is SignUpAction.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.value) }
            is SignUpAction.RegisterClicked -> register(action.onSuccess)
        }
    }

    private fun register(onSuccess: () -> Unit) {
        if (uiState.value.password != uiState.value.confirmPassword) {
            _uiState.update { it.copy(error = UiText.DynamicString("Passwords do not match")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
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
