package com.mohamed.devz.feature.onboarding.presentation

sealed interface OnboardingAction {
    data object FinishAction : OnboardingAction
}