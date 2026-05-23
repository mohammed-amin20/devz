package com.mohamed.devz.feature.profile.presentation.view_profile.util

data class ProfileQuestionUiModel(
    val id: Int,
    val title: String,
    val timeAgo: String,
    val votes: Int,
    val answerCount: Int,
    val tags: List<String>
)