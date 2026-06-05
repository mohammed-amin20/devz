package com.mohamed.devz.feature.core.domain.model

data class Answer(
    val id: Int,
    val description: String,
    val accepted: Boolean,
    val votedIds: String,
    val questionId: Int,
    val accountId: Int,
    val createdAt: String?
)
