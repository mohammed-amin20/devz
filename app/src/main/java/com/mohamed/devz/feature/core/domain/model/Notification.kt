package com.mohamed.devz.feature.core.domain.model

data class Notification(
    val id: Int,
    val description: String,
    val accountId: Int,
    val typeId: Int,
    val seen: Boolean,
    val createdAt: String
)
