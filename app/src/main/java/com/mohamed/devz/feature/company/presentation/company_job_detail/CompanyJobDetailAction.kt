package com.mohamed.devz.feature.company.presentation.company_job_detail

sealed interface CompanyJobDetailAction {
    data class LoadJob(val jobId: Int) : CompanyJobDetailAction
    data class ApproveApplication(val applicationId: Int, val applicantId: Int) : CompanyJobDetailAction
}
