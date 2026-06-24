package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Int,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
    val email: String,
    val password: String,
    @SerialName("image_url")
    val imageUrl: String,
    val bio: String,
    @SerialName("tech_stack")
    val techStack: String,
    @SerialName("github_url")
    val githubUrl: String,
    @SerialName("linkedin_url")
    val linkedInUrl: String,
    @SerialName("website_url")
    val websiteUrl: String,
    @SerialName("points")
    val points: Int = 0,
    @SerialName("fcm_token")
    val fcmToken: String = "",
)
