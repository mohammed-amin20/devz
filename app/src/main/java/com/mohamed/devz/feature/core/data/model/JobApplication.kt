package com.mohamed.devz.feature.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobApplication(
    val id: Int = 0,
    @SerialName("job_id") val jobId: Int = 0,
    @SerialName("applicant_id") val applicantId: Int = 0,
    @SerialName("cover_letter") val coverLetter: String = "",
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String = "",
)
