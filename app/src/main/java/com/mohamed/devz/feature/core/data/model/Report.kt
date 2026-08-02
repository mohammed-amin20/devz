package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: Int = 0,
    @SerialName("reporter_id") val reporterId: Int = 0,
    @SerialName("reported_type") val reportedType: String = "",
    @SerialName("reported_id") val reportedId: Int = 0,
    val reason: String = "",
    val details: String = "",
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String = "",
)
