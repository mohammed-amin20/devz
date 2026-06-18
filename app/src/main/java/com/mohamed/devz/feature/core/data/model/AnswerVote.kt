package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerVote(
    val id: Int = 0,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("answer_id")
    val answerId: Int,
    @SerialName("is_upvote")
    val isUpvote: Boolean = true,
)
