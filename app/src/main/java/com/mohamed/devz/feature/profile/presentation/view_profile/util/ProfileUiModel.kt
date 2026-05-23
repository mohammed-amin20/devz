package com.mohamed.devz.feature.profile.presentation.view_profile.util

data class ProfileUiModel(
    val fullName: String,
    val username: String,
    val points: String,
    val answerCount: Int,
    val questionCount: Int,
    val acceptedRate: String,
    val globalRank: String,
    val skills: List<String>
)