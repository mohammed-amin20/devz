package com.mohamed.devz.feature.profile.presentation.view_profile

sealed interface ProfileAction {
    data object Refresh : ProfileAction
    data object Logout : ProfileAction
    data class ToggleFollow(val targetAccountId: Int) : ProfileAction
}