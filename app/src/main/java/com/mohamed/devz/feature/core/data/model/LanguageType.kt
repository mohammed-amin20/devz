package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LanguageType(
    val id: Int,
    val type: String
)
