package com.mohamed.devz.feature.profile.presentation.edit_profile

data class EditProfileState(
    val fullName: String = "",
    val username: String = "",
    val bio: String = "",
    val github: String = "",
    val linkedin: String = "",
    val website: String = "",
    val skills: List<String> = emptyList(),
    val skillInput: String = "",
    val showSkillInput: Boolean = false,
    val isPublicProfile: Boolean = true,
    val displayEmail: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)