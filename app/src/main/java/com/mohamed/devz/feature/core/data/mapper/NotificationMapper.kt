package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Notification as DataNotification
import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification

fun DataNotification.toDomain(): DomainNotification = DomainNotification(
    id = id,
    description = description,
    accountId = accountId,
    typeId = typeId,
    seen = seen,
    createdAt = createdAt,
)

fun DomainNotification.toData(): DataNotification = DataNotification(
    id = id,
    description = description,
    accountId = accountId,
    typeId = typeId,
    seen = seen,
    createdAt = createdAt,
)
