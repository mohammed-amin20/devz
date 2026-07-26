package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: Int,
    val title: String,
    val message: String,
    @SerialName("created_at")
    val createdAt: String? = null,
)
