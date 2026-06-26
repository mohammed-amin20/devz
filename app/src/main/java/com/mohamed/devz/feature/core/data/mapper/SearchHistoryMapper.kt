package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.SearchHistory as DataSearchHistory
import com.mohamed.devz.feature.core.domain.model.SearchHistory as DomainSearchHistory

fun DataSearchHistory.toDomain(): DomainSearchHistory = DomainSearchHistory(
    id = id,
    accountId = accountId,
    query = query,
    createdAt = createdAt,
)

fun DomainSearchHistory.toData(): DataSearchHistory = DataSearchHistory(
    id = id,
    accountId = accountId,
    query = query,
    createdAt = createdAt ?: "",
)
