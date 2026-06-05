package com.mohamed.devz.feature.core.domain.model

data class Question(
    val id: Int,
    val title: String,
    val description: String,
    val code: String,
    val likesCount: Int,
    val answersCount: Int,
    val tags: String,
    val langTypeId: Int,
    val accountId: Int,
    val createdAt: String?
)