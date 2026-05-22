package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val id: Int,
    val description: String,
    val accepted: Boolean,
    @SerialName("voted_ids")
    val votedIds: String,
    @SerialName("question_id")
    val questionId: Int,
    @SerialName("account_id")
    val accountId: Int,
    @SerialName("created_at")
    val createdAt: String
)
