package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int,
    val description: String,
    @SerialName("account_id")
    val accountId: Int,
    @SerialName("type_id")
    val typeId: Int,
    val seen: Boolean,
    @SerialName("created_at")
    val createdAt: String
)
