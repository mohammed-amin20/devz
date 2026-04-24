package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

data class SignUpScreenState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)