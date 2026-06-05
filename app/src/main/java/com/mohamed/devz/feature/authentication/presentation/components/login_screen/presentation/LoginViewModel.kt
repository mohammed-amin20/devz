package com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UsernameChanged -> _uiState.update { it.copy(username = action.value) }
            is LoginAction.PasswordChanged -> _uiState.update { it.copy(password = action.value) }
            is LoginAction.LoginClicked -> login(action.onSuccess)
        }
    }

    private fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            when (val result = accountRepository.getByUsernameAndPassword(state.username, state.password)) {
                is com.mohamed.devz.feature.core.domain.util.Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    if (result.data != null) {
                        userPreferencesRepository.setLoggedIn()
                        userPreferencesRepository.setAccountId(result.data.id)
                        onSuccess()
                    } else {
                        _uiState.update { it.copy(error = UiText.DynamicString("Invalid credentials"), isLoading = false) }
                    }
                }
                is com.mohamed.devz.feature.core.domain.util.Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }
}
