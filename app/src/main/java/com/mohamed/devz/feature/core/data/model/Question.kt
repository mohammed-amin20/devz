package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val title: String,
    val description: String,
    val code: String,
    @SerialName("likes_count")
    val likesCount: Int,
    @SerialName("answers_count")
    val answersCount: Int,
    val tags: String,
    @SerialName("lang_type_id")
    val langTypeId: Int,
    @SerialName("account_id")
    val accountId: Int,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("like_accounts_ids")
    val likedAccountIds: String = "",
)
