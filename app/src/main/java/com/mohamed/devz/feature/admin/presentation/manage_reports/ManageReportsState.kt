package com.mohamed.devz.feature.admin.presentation.manage_reports

import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ReportUiModel(
    val report: Report,
    val reporterName: String = "Unknown",
    val reporterAvatar: String = "",
    val targetTitle: String = "",
    val ownerAccountId: Int = 0,
)

data class ManageReportsState(
    val allReports: List<ReportUiModel> = emptyList(),
    val filteredReports: List<ReportUiModel> = emptyList(),
    val selectedFilter: Int = 0,
    val selectedReport: ReportUiModel? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isProcessing: Boolean = false,
    val error: UiText? = null,
    val lastActionMessage: String? = null,
)
