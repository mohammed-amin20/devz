package com.mohamed.devz.feature.core.domain.model

data class JobApplication(
    val id: Int = 0,
    val jobId: Int = 0,
    val applicantId: Int = 0,
    val coverLetter: String = "",
    val status: String = "pending",
    val createdAt: String = "",
)
