package com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation

sealed interface LoginAction {
    data class UsernameChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data class LoginClicked(val onSuccess: () -> Unit) : LoginAction
}
