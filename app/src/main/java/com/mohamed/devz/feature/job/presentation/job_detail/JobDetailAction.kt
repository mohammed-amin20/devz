package com.mohamed.devz.feature.job.presentation.job_detail

import com.mohamed.devz.feature.report.presentation.ReportTarget

sealed interface JobDetailAction {
    data class LoadJob(val jobId: Int) : JobDetailAction
    data class EmailChanged(val value: String) : JobDetailAction
    data class WhatsAppChanged(val value: String) : JobDetailAction
    data class CoverLetterChanged(val value: String) : JobDetailAction
    data class SubmitApplication(val onSuccess: () -> Unit) : JobDetailAction
    data object DismissError : JobDetailAction
    data class ShowReport(val target: ReportTarget) : JobDetailAction
    data object DismissReport : JobDetailAction
}
