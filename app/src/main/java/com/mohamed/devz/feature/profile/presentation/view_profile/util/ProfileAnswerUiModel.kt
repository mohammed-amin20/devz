package com.mohamed.devz.feature.profile.presentation.view_profile.util

data class ProfileAnswerUiModel(
    val id: Int,
    val questionId: Int,
    val questionTitle: String,
    val preview: String,
    val likes: Int,
    val timeAgo: String,
    val isAccepted: Boolean
)