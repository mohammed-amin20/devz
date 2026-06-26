package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchHistory(
    val id: Int = 0,
    @SerialName("account_id")
    val accountId: Int,
    val query: String,
    @SerialName("created_at")
    val createdAt: String = "",
)
