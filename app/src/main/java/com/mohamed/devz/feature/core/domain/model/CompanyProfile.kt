package com.mohamed.devz.feature.core.domain.model

data class CompanyProfile(
    val id: Int = 0,
    val userId: Int = 0,
    val companyName: String = "",
    val logoUrl: String = "",
    val website: String = "",
    val description: String = "",
    val subscriptionStatus: String = "pending",
    val subscriptionExpiry: String? = null,
    val createdAt: String = "",
    val bio: String = "",
    val location: String = "",
    val industry: String = "",
    val twitterUrl: String = "",
    val rating: Double = 0.0,
    val isVerified: Boolean = false,
)
