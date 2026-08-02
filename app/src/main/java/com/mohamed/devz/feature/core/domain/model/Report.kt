package com.mohamed.devz.feature.core.domain.model

data class Report(
    val id: Int,
    val reporterId: Int,
    val reportedType: String,
    val reportedId: Int,
    val reason: String,
    val details: String = "",
    val status: String = "pending",
    val createdAt: String,
    val reporterName: String? = null,
    val reporterAvatarUrl: String? = null,
    val targetTitle: String? = null,
)
