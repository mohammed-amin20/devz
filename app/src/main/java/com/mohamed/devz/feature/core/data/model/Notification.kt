package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("actor_id")
    val actorId: Int,
    @SerialName("question_id")
    val questionId: Int,
    @SerialName("answer_id")
    val answerId: Int? = null,
    val type: String,
    val message: String,
    @SerialName("is_read")
    val isRead: Boolean = false,
    @SerialName("created_at")
    val createdAt: String = "",
)
