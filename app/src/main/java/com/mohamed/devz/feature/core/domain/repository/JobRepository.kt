package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.JobApplication
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.Error

interface JobRepository {
    suspend fun getApprovedJobPostings(): Result<List<JobPosting>, Error>
    suspend fun getJobPostingById(id: Int): Result<JobPosting, Error>
    suspend fun insertApplication(application: JobApplication): Result<JobApplication, Error>
    suspend fun getApplicationsByApplicantId(applicantId: Int): Result<List<JobApplication>, Error>
}
