package com.mohamed.devz.feature.core.domain.model

data class Announcement(
    val id: Int,
    val title: String,
    val message: String,
    val createdAt: String? = null,
)
