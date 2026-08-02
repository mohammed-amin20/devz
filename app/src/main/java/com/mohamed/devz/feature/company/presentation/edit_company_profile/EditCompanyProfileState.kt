package com.mohamed.devz.feature.company.presentation.edit_company_profile

import com.mohamed.devz.feature.core.presentation.util.UiText

data class EditCompanyProfileState(
    val id: Int = 0,
    val userId: Int = 0,
    val companyName: String = "",
    val logoUrl: String = "",
    val website: String = "",
    val description: String = "",
    val bio: String = "",
    val location: String = "",
    val industry: String = "",
    val twitterUrl: String = "",
    val subscriptionStatus: String = "pending",
    val subscriptionExpiry: String? = null,
    val createdAt: String = "",
    val isVerified: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val error: UiText? = null,
)
