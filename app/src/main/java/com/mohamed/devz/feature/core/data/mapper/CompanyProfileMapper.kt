package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.CompanyProfile as DataCompanyProfile
import com.mohamed.devz.feature.core.domain.model.CompanyProfile as DomainCompanyProfile

fun DataCompanyProfile.toDomain(): DomainCompanyProfile = DomainCompanyProfile(
    id = id,
    userId = userId,
    companyName = companyName,
    logoUrl = logoUrl,
    website = website,
    description = description,
    subscriptionStatus = subscriptionStatus,
    subscriptionExpiry = subscriptionExpiry,
    createdAt = createdAt,
)

fun DomainCompanyProfile.toData(): DataCompanyProfile = DataCompanyProfile(
    id = id,
    userId = userId,
    companyName = companyName,
    logoUrl = logoUrl,
    website = website,
    description = description,
    subscriptionStatus = subscriptionStatus,
    subscriptionExpiry = subscriptionExpiry,
    createdAt = createdAt,
)
