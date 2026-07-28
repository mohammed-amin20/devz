package com.mohamed.devz.feature.core.domain.model

data class JobPosting(
    val id: Int = 0,
    val companyName: String = "",
    val title: String = "",
    val description: String = "",
    val salaryRange: String = "",
    val jobType: String = "full-time",
    val status: String = "approved",
    val createdAt: String = "",
    val accountId: Int = 0,
)
