package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Answer as DataAnswer
import com.mohamed.devz.feature.core.domain.model.Answer as DomainAnswer

fun DataAnswer.toDomain(): DomainAnswer = DomainAnswer(
    id = id,
    description = description,
    accepted = accepted,
    votedIds = votedIds,
    questionId = questionId,
    accountId = accountId,
    createdAt = createdAt,
)

fun DomainAnswer.toData(): DataAnswer = DataAnswer(
    id = id,
    description = description,
    accepted = accepted,
    votedIds = votedIds,
    questionId = questionId,
    accountId = accountId,
    createdAt = createdAt,
)
