package com.mohamed.devz.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import jakarta.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.FinishAction -> finish()
        }
    }

    private fun finish() {
        viewModelScope.launch {
            userPreferencesRepository.setNotFirstTime()
        }
    }
}
