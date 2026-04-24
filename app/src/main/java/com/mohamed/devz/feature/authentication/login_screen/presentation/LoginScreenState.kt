package com.mohamed.devz.feature.authentication.login_screen.presentation

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)