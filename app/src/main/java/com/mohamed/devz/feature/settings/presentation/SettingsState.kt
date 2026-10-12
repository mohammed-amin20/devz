package com.mohamed.devz.feature.settings.presentation

import com.mohamed.devz.ui.theme.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.default,
    val language: AppLanguage = AppLanguage.default,
    val isLoading: Boolean = true,
)
