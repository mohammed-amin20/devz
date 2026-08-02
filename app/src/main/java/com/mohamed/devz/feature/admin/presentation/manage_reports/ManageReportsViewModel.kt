package com.mohamed.devz.feature.admin.presentation.manage_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.ReportRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val jobRepository: JobRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageReportsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun onAction(action: ManageReportsAction) {
        when (action) {
            is ManageReportsAction.FilterSelected -> {
                _uiState.update { it.copy(selectedFilter = action.index) }
                applyFilters()
            }
            is ManageReportsAction.LoadReports -> loadReports()
            is ManageReportsAction.Refresh -> loadReports(isRefresh = true)
            is ManageReportsAction.SelectReport -> {
                val matched = _uiState.value.allReports.find { it.report.id == action.report.id }
                _uiState.update { it.copy(selectedReport = matched ?: ReportUiModel(report = action.report)) }
            }
            is ManageReportsAction.DismissDetail -> {
                _uiState.update { it.copy(selectedReport = null) }
            }
            is ManageReportsAction.DismissReport -> dismissReport(action.report)
            is ManageReportsAction.DeleteContent -> deleteContent(action.report)
            is ManageReportsAction.BanUser -> banUser(action.report)
        }
    }

    private fun loadReports(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }

            when (val result = reportRepository.getAll()) {
                is Result.Success -> {
                    val reports = result.data.sortedByDescending { it.createdAt }
                    val models = reports.map { report -> resolveReport(report) }
                    _uiState.update {
                        it.copy(allReports = models, isLoading = false, isRefreshing = false)
                    }
                    applyFilters()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error.toUIText(),
                        )
                    }
                }
            }
        }
    }

    private suspend fun resolveReport(report: Report): ReportUiModel {
        val reporter = (accountRepository.getById(report.reporterId) as? Result.Success)?.data
        val targetTitle = when (report.reportedType) {
            "question" -> (questionRepository.getById(report.reportedId) as? Result.Success)?.data?.title
            "answer" -> {
                val answer = (answerRepository.getById(report.reportedId) as? Result.Success)?.data
                answer?.description?.take(80)
            }
            "job" -> (jobRepository.getJobPostingById(report.reportedId) as? Result.Success)?.data?.title
            "user" -> (accountRepository.getById(report.reportedId) as? Result.Success)?.data?.fullName
            else -> null
        }
        val ownerAccountId = when (report.reportedType) {
            "question" -> (questionRepository.getById(report.reportedId) as? Result.Success)?.data?.accountId ?: 0
            "answer" -> (answerRepository.getById(report.reportedId) as? Result.Success)?.data?.accountId ?: 0
            "job" -> (jobRepository.getJobPostingById(report.reportedId) as? Result.Success)?.data?.accountId ?: 0
            "user" -> report.reportedId
            else -> 0
        }
        return ReportUiModel(
            report = report,
            reporterName = reporter?.fullName ?: reporter?.username ?: "Unknown",
            reporterAvatar = reporter?.imageUrl ?: "",
            targetTitle = targetTitle ?: "",
            ownerAccountId = ownerAccountId,
        )
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = when (state.selectedFilter) {
            1 -> state.allReports.filter { it.report.status == "pending" }
            2 -> state.allReports.filter { it.report.status == "reviewed" }
            3 -> state.allReports.filter { it.report.status == "dismissed" }
            else -> state.allReports
        }
        _uiState.update { it.copy(filteredReports = filtered) }
    }

    private fun dismissReport(report: Report) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            when (val result = reportRepository.updateStatus(report.id, "dismissed")) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            selectedReport = null,
                            lastActionMessage = "Report dismissed",
                        )
                    }
                    loadReports()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isProcessing = false, error = result.error.toUIText())
                    }
                }
            }
        }
    }

    private fun deleteContent(report: Report) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val deleteResult = when (report.reportedType) {
                "question" -> questionRepository.delete(report.reportedId)
                "answer" -> {
                    val answer = (answerRepository.getById(report.reportedId) as? Result.Success)?.data
                    if (answer != null) answerRepository.delete(answer)
                    else Result.Error(com.mohamed.devz.feature.core.domain.util.Error.NotFound)
                }
                "job" -> {
                    val job = (jobRepository.getJobPostingById(report.reportedId) as? Result.Success)?.data
                    if (job != null) jobRepository.updateJobPosting(job.copy(status = "deleted"))
                    else Result.Error(com.mohamed.devz.feature.core.domain.util.Error.NotFound)
                }
                else -> Result.Error(com.mohamed.devz.feature.core.domain.util.Error.NotFound)
            }

            when (deleteResult) {
                is Result.Success -> {
                    when (val statusResult = reportRepository.updateStatus(report.id, "reviewed")) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isProcessing = false,
                                    selectedReport = null,
                                    lastActionMessage = "Content deleted",
                                )
                            }
                            loadReports()
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(isProcessing = false, error = statusResult.error.toUIText())
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isProcessing = false, error = deleteResult.error.toUIText())
                    }
                }
            }
        }
    }

    private fun banUser(report: Report) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val ownerId = if (report.reportedType == "user") report.reportedId
            else {
                val matched = _uiState.value.allReports.find { it.report.id == report.id }
                matched?.ownerAccountId ?: 0
            }
            if (ownerId == 0) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = com.mohamed.devz.feature.core.presentation.util.UiText.DynamicString(
                            "Could not identify the user to ban"
                        ),
                    )
                }
                return@launch
            }

            val owner = (accountRepository.getById(ownerId) as? Result.Success)?.data
            if (owner == null) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = com.mohamed.devz.feature.core.presentation.util.UiText.DynamicString(
                            "User not found"
                        ),
                    )
                }
                return@launch
            }

            when (val banResult = accountRepository.update(owner.copy(isBanned = true))) {
                is Result.Success -> {
                    when (val statusResult = reportRepository.updateStatus(report.id, "reviewed")) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isProcessing = false,
                                    selectedReport = null,
                                    lastActionMessage = "User banned",
                                )
                            }
                            loadReports()
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(isProcessing = false, error = statusResult.error.toUIText())
                            }
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isProcessing = false, error = banResult.error.toUIText())
                    }
                }
            }
        }
    }
}
