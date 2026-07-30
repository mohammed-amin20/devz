package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.JobApplication as DataJobApplication
import com.mohamed.devz.feature.core.domain.model.JobApplication as DomainJobApplication

fun DataJobApplication.toDomain(): DomainJobApplication = DomainJobApplication(
    id = id,
    jobId = jobId,
    applicantId = applicantId,
    coverLetter = coverLetter,
    status = status,
    createdAt = createdAt,
    email = email,
    whatsapp = whatsapp,
)

fun DomainJobApplication.toData(): DataJobApplication = DataJobApplication(
    id = id,
    jobId = jobId,
    applicantId = applicantId,
    coverLetter = coverLetter,
    status = status,
    createdAt = createdAt,
    email = email,
    whatsapp = whatsapp,
)
