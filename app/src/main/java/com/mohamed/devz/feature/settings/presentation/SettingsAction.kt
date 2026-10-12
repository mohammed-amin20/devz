package com.mohamed.devz.feature.settings.presentation

import com.mohamed.devz.ui.theme.ThemeMode

sealed interface SettingsAction {
    data class SelectThemeMode(val mode: ThemeMode) : SettingsAction
    data class SelectLanguage(val language: AppLanguage) : SettingsAction
}
