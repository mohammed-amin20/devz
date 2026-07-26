package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Announcement as DataAnnouncement
import com.mohamed.devz.feature.core.domain.model.Announcement as DomainAnnouncement

fun DataAnnouncement.toDomain(): DomainAnnouncement = DomainAnnouncement(
    id = id,
    title = title,
    message = message,
    createdAt = createdAt,
)

fun DomainAnnouncement.toData(): DataAnnouncement = DataAnnouncement(
    id = id,
    title = title,
    message = message,
    createdAt = createdAt,
)
