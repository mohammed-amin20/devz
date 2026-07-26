package com.mohamed.devz.feature.admin.presentation.admin_dashboard

import com.mohamed.devz.feature.core.presentation.util.UiText

data class AdminDashboardState(
    val totalUsers: Int = 0,
    val totalQuestions: Int = 0,
    val totalAnswers: Int = 0,
    val bannedUsers: Int = 0,
    val isLoading: Boolean = true,
    val error: UiText? = null,
)
