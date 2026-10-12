package com.mohamed.devz.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.observeThemeMode(),
                userPreferencesRepository.observeLanguage(),
            ) { theme, language ->
                ThemeMode.fromStorage(theme) to AppLanguage.fromStorage(language)
            }.collect { (theme, language) ->
                _uiState.update {
                    it.copy(themeMode = theme, language = language, isLoading = false)
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SelectThemeMode -> viewModelScope.launch {
                userPreferencesRepository.setThemeMode(action.mode.storageKey)
            }

            is SettingsAction.SelectLanguage -> viewModelScope.launch {
                userPreferencesRepository.setLanguage(action.language.storageKey)
            }
        }
    }
}
