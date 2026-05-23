package com.mohamed.devz.feature.profile.presentation.view_profile

sealed interface ProfileAction {
    data object Refresh : ProfileAction
}