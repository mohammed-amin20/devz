package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

sealed interface SignUpAction {
    data class FullNameChanged(val value: String) : SignUpAction
    data class UsernameChanged(val value: String) : SignUpAction
    data class EmailChanged(val value: String) : SignUpAction
    data class PasswordChanged(val value: String) : SignUpAction
    data class ConfirmPasswordChanged(val value: String) : SignUpAction
    data class RegisterClicked(val onSuccess: () -> Unit) : SignUpAction
}
