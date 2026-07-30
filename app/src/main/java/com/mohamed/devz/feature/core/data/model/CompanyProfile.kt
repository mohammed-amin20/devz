package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyProfile(
    val id: Int = 0,
    @SerialName("user_id")
    val userId: Int = 0,
    @SerialName("company_name")
    val companyName: String = "",
    @SerialName("logo_url")
    val logoUrl: String = "",
    val website: String = "",
    val description: String = "",
    @SerialName("subscription_status")
    val subscriptionStatus: String = "pending",
    @SerialName("subscription_expiry")
    val subscriptionExpiry: String? = null,
    @SerialName("created_at")
    val createdAt: String = "",
)
