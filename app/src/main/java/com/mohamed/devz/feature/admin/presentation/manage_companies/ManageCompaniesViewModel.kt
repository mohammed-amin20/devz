package com.mohamed.devz.feature.admin.presentation.manage_companies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCompaniesViewModel @Inject constructor(
    private val companyProfileRepository: CompanyProfileRepository,
    private val accountRepository: AccountRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageCompaniesState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onAction(action: ManageCompaniesAction) {
        when (action) {
            ManageCompaniesAction.Load -> load()
            is ManageCompaniesAction.ToggleSubscription -> toggleSubscription(action.profileId, action.activate)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = companyProfileRepository.getAll()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            companies = result.data.map { profile ->
                                CompanyUiModel(
                                    id = profile.id,
                                    companyName = profile.companyName,
                                    website = profile.website,
                                    subscriptionStatus = profile.subscriptionStatus,
                                    accountId = profile.userId,
                                )
                            },
                            isLoading = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }

    private fun toggleSubscription(profileId: Int, activate: Boolean) {
        viewModelScope.launch {
            val profileResult = companyProfileRepository.getByAccountId(profileId)
            val profile = (profileResult as? Result.Success)?.data ?: return@launch

            val newStatus = if (activate) "active" else "pending"
            val updated = profile.copy(subscriptionStatus = newStatus)

            when (companyProfileRepository.update(updated)) {
                is Result.Success -> {
                    val accountResult = accountRepository.getById(profile.userId)
                    val account = (accountResult as? Result.Success)?.data
                    if (account != null && account.fcmToken.isNotBlank()) {
                        val title = if (activate) "Subscription Activated" else "Subscription Deactivated"
                        val body = if (activate) "Your company subscription is now active. You can post jobs!" else "Your company subscription has been deactivated."
                        fcmPushSender.sendPush(
                            fcmToken = account.fcmToken,
                            title = title,
                            body = body,
                            questionId = null,
                            type = "subscription",
                            actorId = null,
                        )
                    }
                    load()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = UiText.DynamicString("Failed to update subscription")) }
                }
            }
        }
    }
}
