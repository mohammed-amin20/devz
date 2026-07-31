package com.mohamed.devz.feature.admin.presentation.manage_jobs

import com.mohamed.devz.feature.core.domain.model.JobPosting

sealed interface ManageJobsAction {
    data object LoadJobs : ManageJobsAction
    data object Refresh : ManageJobsAction
    data class ApproveJob(val job: JobPosting) : ManageJobsAction
    data class RejectJob(val job: JobPosting) : ManageJobsAction
    data object DismissDialog : ManageJobsAction
}
