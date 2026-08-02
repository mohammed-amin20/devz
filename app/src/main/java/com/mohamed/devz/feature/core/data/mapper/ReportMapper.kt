package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Report as DataReport
import com.mohamed.devz.feature.core.domain.model.Report as DomainReport

fun DataReport.toDomain(): DomainReport = DomainReport(
    id = id,
    reporterId = reporterId,
    reportedType = reportedType,
    reportedId = reportedId,
    reason = reason,
    details = details,
    status = status,
    createdAt = createdAt,
)

fun DomainReport.toData(): DataReport = DataReport(
    id = id,
    reporterId = reporterId,
    reportedType = reportedType,
    reportedId = reportedId,
    reason = reason,
    details = details,
    status = status,
    createdAt = createdAt,
)
