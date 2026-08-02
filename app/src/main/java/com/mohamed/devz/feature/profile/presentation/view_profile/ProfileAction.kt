package com.mohamed.devz.feature.profile.presentation.view_profile

import com.mohamed.devz.feature.report.presentation.ReportTarget

sealed interface ProfileAction {
    data object Refresh : ProfileAction
    data object Logout : ProfileAction
    data class ToggleFollow(val targetAccountId: Int) : ProfileAction
    data object ShowFollowers : ProfileAction
    data object ShowFollowing : ProfileAction
    data object DismissDialog : ProfileAction
    data object LoadApplications : ProfileAction
    data class SetTargetAccountId(val accountId: Int?) : ProfileAction
    data class ShowReport(val target: ReportTarget) : ProfileAction
    data object DismissReport : ProfileAction
}