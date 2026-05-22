package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.NotificationType as DataNotificationType
import com.mohamed.devz.feature.core.domain.model.NotificationType as DomainNotificationType

fun DataNotificationType.toDomain(): DomainNotificationType = DomainNotificationType(
    id = id,
    type = type,
)

fun DomainNotificationType.toData(): DataNotificationType = DataNotificationType(
    id = id,
    type = type,
)
