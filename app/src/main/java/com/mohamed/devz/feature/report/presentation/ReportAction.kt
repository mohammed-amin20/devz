package com.mohamed.devz.feature.report.presentation

sealed interface ReportAction {
    data class ReasonSelected(val reason: String) : ReportAction
    data class DetailsChanged(val value: String) : ReportAction
    data class Submit(val target: ReportTarget) : ReportAction
    data object Reset : ReportAction
}
