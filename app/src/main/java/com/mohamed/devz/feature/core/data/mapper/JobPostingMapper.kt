package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.JobPosting as DataJobPosting
import com.mohamed.devz.feature.core.domain.model.JobPosting as DomainJobPosting

fun DataJobPosting.toDomain(): DomainJobPosting = DomainJobPosting(
    id = id,
    companyName = companyName,
    title = title,
    description = description,
    salaryRange = salaryRange,
    jobType = jobType,
    status = status,
    createdAt = createdAt,
    accountId = accountId,
)

fun DomainJobPosting.toData(): DataJobPosting = DataJobPosting(
    id = id,
    companyName = companyName,
    title = title,
    description = description,
    salaryRange = salaryRange,
    jobType = jobType,
    status = status,
    createdAt = createdAt,
    accountId = accountId,
)
