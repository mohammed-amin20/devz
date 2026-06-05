package com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation

import com.mohamed.devz.feature.core.presentation.util.UiText

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)