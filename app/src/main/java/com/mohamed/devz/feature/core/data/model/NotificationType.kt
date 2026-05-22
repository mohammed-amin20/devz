package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationType(
    val id: Int,
    val type: String
)