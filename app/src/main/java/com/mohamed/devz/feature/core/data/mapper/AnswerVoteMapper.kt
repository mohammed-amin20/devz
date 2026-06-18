package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.AnswerVote as DataAnswerVote
import com.mohamed.devz.feature.core.domain.model.AnswerVote as DomainAnswerVote

fun DataAnswerVote.toDomain(): DomainAnswerVote = DomainAnswerVote(
    id = id,
    userId = userId,
    answerId = answerId,
    isUpvote = isUpvote,
)
