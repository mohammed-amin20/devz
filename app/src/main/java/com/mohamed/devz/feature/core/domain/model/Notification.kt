package com.mohamed.devz.feature.core.domain.model

data class Notification(
    val id: Int,
    val description: String,
    val actorName: String?,
    val type: String,
    val seen: Boolean,
    val createdAt: String
)
