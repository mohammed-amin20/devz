package com.mohamed.devz.feature.job.presentation.jobs_screen

sealed interface JobsAction {
    data object LoadJobs : JobsAction
    data class FilterByType(val type: String?) : JobsAction
}
