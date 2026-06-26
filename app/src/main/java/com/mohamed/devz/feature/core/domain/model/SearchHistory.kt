package com.mohamed.devz.feature.core.domain.model

data class SearchHistory(
    val id: Int,
    val accountId: Int,
    val query: String,
    val createdAt: String?,
)
