package com.mohamed.devz.feature.report.presentation

import com.mohamed.devz.feature.core.presentation.util.UiText

data class ReportState(
    val reason: String? = null,
    val details: String = "",
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val error: UiText? = null,
)
