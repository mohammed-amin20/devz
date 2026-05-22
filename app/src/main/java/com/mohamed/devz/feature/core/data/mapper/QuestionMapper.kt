package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Question as DataQuestion
import com.mohamed.devz.feature.core.domain.model.Question as DomainQuestion

fun DataQuestion.toDomain(): DomainQuestion = DomainQuestion(
    id = id,
    title = title,
    description = description,
    code = code,
    likesCount = likesCount,
    answersCount = answersCount,
    tags = tags,
    langTypeId = langTypeId,
    accountId = accountId,
    createdAt = createdAt,
)

fun DomainQuestion.toData(): DataQuestion = DataQuestion(
    id = id,
    title = title,
    description = description,
    code = code,
    likesCount = likesCount,
    answersCount = answersCount,
    tags = tags,
    langTypeId = langTypeId,
    accountId = accountId,
    createdAt = createdAt,
)
