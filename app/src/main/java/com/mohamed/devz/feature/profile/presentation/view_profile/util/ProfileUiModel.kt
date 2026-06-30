package com.mohamed.devz.feature.profile.presentation.view_profile.util

data class ProfileUiModel(
    val fullName: String,
    val username: String,
    val imageUrl: String,
    val bio: String,
    val points: String,
    val answerCount: Int,
    val questionCount: Int,
    val acceptedRate: String,
    val skills: List<String>,
    val githubUrl: String,
    val linkedInUrl: String,
    val websiteUrl: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
)