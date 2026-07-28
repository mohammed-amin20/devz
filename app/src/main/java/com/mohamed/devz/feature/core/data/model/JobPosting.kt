package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobPosting(
    val id: Int = 0,
    @SerialName("company_name") val companyName: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("salary_range") val salaryRange: String = "",
    @SerialName("job_type") val jobType: String = "full-time",
    val status: String = "approved",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("account_id") val accountId: Int = 0,
)
