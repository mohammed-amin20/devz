package com.mohamed.devz.feature.job.presentation.post_job

sealed interface PostJobAction {
    data class TitleChanged(val value: String) : PostJobAction
    data class DescriptionChanged(val value: String) : PostJobAction
    data class SalaryRangeChanged(val value: String) : PostJobAction
    data class JobTypeSelected(val value: String) : PostJobAction
    data class Submit(val onSuccess: () -> Unit) : PostJobAction
}
