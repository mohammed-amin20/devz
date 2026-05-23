package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Notification as DataNotification
import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification

fun DataNotification.toDomain(typeString: String, actorName: String?): DomainNotification = DomainNotification(
    id = id,
    description = description,
    actorName = actorName,
    type = typeString,
    seen = seen,
    createdAt = createdAt,
)
