package com.mohamed.devz.feature.core.domain.model

data class Account(
    val id: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val imageUrl: String,
    val bio: String,
    val techStack: String,
    val githubUrl: String,
    val linkedInUrl: String,
    val websiteUrl: String,
    val points: Int = 0,
    val fcmToken: String = "",
    val followerIds: String = "",
    val followingIds: String = "",
)
