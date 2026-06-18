package com.mohamed.devz.feature.core.domain.model

data class AnswerVote(
    val id: Int,
    val userId: Int,
    val answerId: Int,
    val isUpvote: Boolean,
)
