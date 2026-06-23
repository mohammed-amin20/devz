package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.Account as DataAccount
import com.mohamed.devz.feature.core.domain.model.Account as DomainAccount

fun DataAccount.toDomain(): DomainAccount = DomainAccount(
    id = id,
    username = username,
    fullName = fullName,
    email = email,
    password = password,
    imageUrl = imageUrl,
    bio = bio,
    techStack = techStack,
    githubUrl = githubUrl,
    linkedInUrl = linkedInUrl,
    websiteUrl = websiteUrl,
    points = points,
)

fun DomainAccount.toData(): DataAccount = DataAccount(
    id = id,
    username = username,
    fullName = fullName,
    email = email,
    password = password,
    imageUrl = imageUrl,
    bio = bio,
    techStack = techStack,
    githubUrl = githubUrl,
    linkedInUrl = linkedInUrl,
    websiteUrl = websiteUrl,
    points = points,
)
