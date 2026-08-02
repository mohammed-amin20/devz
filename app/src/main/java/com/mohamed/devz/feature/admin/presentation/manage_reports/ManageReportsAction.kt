package com.mohamed.devz.feature.admin.presentation.manage_reports

import com.mohamed.devz.feature.core.domain.model.Report

sealed interface ManageReportsAction {
    data class FilterSelected(val index: Int) : ManageReportsAction
    data object LoadReports : ManageReportsAction
    data object Refresh : ManageReportsAction
    data class SelectReport(val report: Report) : ManageReportsAction
    data object DismissDetail : ManageReportsAction
    data class DismissReport(val report: Report) : ManageReportsAction
    data class DeleteContent(val report: Report) : ManageReportsAction
    data class BanUser(val report: Report) : ManageReportsAction
}
