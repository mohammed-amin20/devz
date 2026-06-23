package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Notification as DataNotification
import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification

fun DataNotification.toDomain(
    actorName: String? = null,
    actorAvatarUrl: String? = null,
): DomainNotification = DomainNotification(
    id = id,
    typeId = typeId,
    userId = userId,
    actorId = actorId,
    questionId = questionId,
    answerId = answerId,
    type = type,
    message = message,
    isRead = isRead,
    createdAt = createdAt,
    actorName = actorName,
    actorAvatarUrl = actorAvatarUrl,
)

fun DomainNotification.toData(): DataNotification = DataNotification(
    id = id,
    typeId = typeId,
    userId = userId,
    actorId = actorId,
    questionId = questionId,
    answerId = answerId,
    type = type,
    message = message,
    isRead = isRead,
    createdAt = createdAt,
)
