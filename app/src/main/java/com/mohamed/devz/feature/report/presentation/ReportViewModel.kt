package com.mohamed.devz.feature.report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationTypeRepository
import com.mohamed.devz.feature.core.domain.repository.ReportRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val accountRepository: AccountRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationTypeRepository: NotificationTypeRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: ReportAction) {
        when (action) {
            is ReportAction.ReasonSelected -> {
                _uiState.update { it.copy(reason = action.reason, error = null) }
            }
            is ReportAction.DetailsChanged -> {
                _uiState.update { it.copy(details = action.value) }
            }
            is ReportAction.Submit -> submit(action.target)
            is ReportAction.Reset -> _uiState.value = ReportState()
        }
    }

    private fun submit(target: ReportTarget) {
        val reason = _uiState.value.reason
        if (reason == null) {
            _uiState.update { it.copy(error = UiText.DynamicString("Please select a reason.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            val reporterId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (reporterId == 0) {
                _uiState.update {
                    it.copy(
                        error = UiText.DynamicString("Please sign in to report."),
                        isSubmitting = false,
                    )
                }
                return@launch
            }

            when (val existing = reportRepository.getByReporterAndTarget(reporterId, target.reportedType, target.reportedId)) {
                is Result.Success -> {
                    if (existing.data != null) {
                        _uiState.update {
                            it.copy(
                                error = UiText.DynamicString("You have already reported this."),
                                isSubmitting = false,
                            )
                        }
                        return@launch
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = existing.error.toUIText(), isSubmitting = false) }
                    return@launch
                }
            }

            val report = Report(
                id = 0,
                reporterId = reporterId,
                reportedType = target.reportedType,
                reportedId = target.reportedId,
                reason = reason,
                details = _uiState.value.details.trim(),
                status = "pending",
                createdAt = java.time.LocalDateTime.now().toString(),
            )

            when (val result = reportRepository.insert(report)) {
                is Result.Success -> {
                    notifyAdmins(reporterId, target, result.data.id)
                    _uiState.update { it.copy(isSubmitting = false, submitted = true, error = null) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isSubmitting = false) }
                }
            }
        }
    }

    private suspend fun notifyAdmins(reporterId: Int, target: ReportTarget, reportId: Int) {
        val accountsResult = accountRepository.getAll()
        val admins = (accountsResult as? Result.Success)?.data?.filter { it.isAdmin } ?: emptyList()
        if (admins.isEmpty()) return

        val reporterName = (accountsResult as? Result.Success)?.data
            ?.firstOrNull { it.id == reporterId }?.username

        val typeResult = notificationTypeRepository.getAll()
        val typeId = when (val result = typeResult) {
            is Result.Success -> result.data.firstOrNull { it.type.equals("system", true) }?.id
                ?: result.data.firstOrNull()?.id
            is Result.Error -> null
        } ?: 1

        val label = target.reportedType.replaceFirstChar { it.uppercase() }
        val byLine = reporterName?.let { "@$it" } ?: "someone"
        val message = "New $label report by $byLine\n${target.preview.take(60)}"
        val now = java.time.LocalDateTime.now().toString()
        admins.forEach { admin ->
            notificationRepository.insert(
                Notification(
                    id = 0,
                    typeId = typeId,
                    userId = admin.id,
                    actorId = reporterId,
                    questionId = 0,
                    answerId = null,
                    type = "system",
                    message = message,
                    isRead = false,
                    createdAt = now,
                    senderType = "system",
                    isGlobal = false,
                )
            )
            if (admin.fcmToken.isNotBlank()) {
                fcmPushSender.sendPush(
                    fcmToken = admin.fcmToken,
                    title = "New $label report",
                    body = "$byLine: ${target.preview.take(60)}",
                    questionId = null,
                    type = "system",
                    reportId = reportId,
                )
            }
        }
    }
}
