package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

import com.mohamed.devz.feature.core.presentation.util.UiText

data class SignUpState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)