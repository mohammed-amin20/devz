package com.mohamed.devz.feature.core.domain.model

data class Notification(
    val id: Int,
    val typeId: Int,
    val userId: Int,
    val actorId: Int,
    val questionId: Int,
    val answerId: Int?,
    val type: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String,
    val actorName: String? = null,
    val actorAvatarUrl: String? = null,
)
