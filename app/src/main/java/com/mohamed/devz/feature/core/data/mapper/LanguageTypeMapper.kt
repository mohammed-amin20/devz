package com.mohamed.devz.feature.core.data.mapper

import com.mohamed.devz.feature.core.data.model.LanguageType as DataLanguageType
import com.mohamed.devz.feature.core.domain.model.LanguageType as DomainLanguageType

fun DataLanguageType.toDomain(): DomainLanguageType = DomainLanguageType(
    id = id,
    type = type,
)

fun DomainLanguageType.toData(): DataLanguageType = DataLanguageType(
    id = id,
    type = type,
)
