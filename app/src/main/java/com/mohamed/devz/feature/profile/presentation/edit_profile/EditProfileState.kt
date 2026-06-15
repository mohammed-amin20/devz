package com.mohamed.devz.feature.profile.presentation.edit_profile

import com.mohamed.devz.feature.core.presentation.util.UiText

data class EditProfileState(
    val id: Int = 0,
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val bio: String = "",
    val github: String = "",
    val linkedin: String = "",
    val website: String = "",
    val skills: List<String> = emptyList(),
    val skillInput: String = "",
    val showSkillInput: Boolean = false,
    val imageUrl: String = "",
    val localImageBytes: ByteArray? = null,
    val isPublicProfile: Boolean = true,
    val displayEmail: Boolean = false,
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val error: UiText? = null
)