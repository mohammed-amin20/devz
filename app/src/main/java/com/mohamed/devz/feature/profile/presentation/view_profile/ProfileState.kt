package com.mohamed.devz.feature.profile.presentation.view_profile

import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileAnswerUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileFollowerUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileQuestionUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileUiModel

data class ProfileState(
    val id: Int = 0,
    val profile: ProfileUiModel? = null,
    val myQuestions: List<ProfileQuestionUiModel> = emptyList(),
    val myAnswers: List<ProfileAnswerUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isOwnProfile: Boolean = true,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val showFollowersDialog: Boolean = false,
    val showFollowingDialog: Boolean = false,
    val followerAccounts: List<ProfileFollowerUiModel> = emptyList(),
    val followingAccounts: List<ProfileFollowerUiModel> = emptyList(),
    val isLoadingDialog: Boolean = false,
)