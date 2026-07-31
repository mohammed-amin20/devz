package com.mohamed.devz.feature.company.presentation.edit_company_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.CompanyProfile
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
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
class EditCompanyProfileViewModel @Inject constructor(
    private val companyProfileRepository: CompanyProfileRepository,
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCompanyProfileState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: EditCompanyProfileAction) {
        when (action) {
            is EditCompanyProfileAction.PickImage -> uploadImage(action.imageBytes)
            is EditCompanyProfileAction.CompanyNameChanged -> _uiState.update { it.copy(companyName = action.v) }
            is EditCompanyProfileAction.WebsiteChanged -> _uiState.update { it.copy(website = action.v) }
            is EditCompanyProfileAction.DescriptionChanged -> _uiState.update { it.copy(description = action.v) }
            is EditCompanyProfileAction.BioChanged -> _uiState.update { it.copy(bio = action.v) }
            is EditCompanyProfileAction.LocationChanged -> _uiState.update { it.copy(location = action.v) }
            is EditCompanyProfileAction.IndustryChanged -> _uiState.update { it.copy(industry = action.v) }
            is EditCompanyProfileAction.TwitterChanged -> _uiState.update { it.copy(twitterUrl = action.v) }
            EditCompanyProfileAction.ClearError -> _uiState.update { it.copy(error = null) }
            is EditCompanyProfileAction.Save -> save(action.onSave)
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update {
                    it.copy(isLoading = false, error = UiText.DynamicString("Company profile not found"))
                }
                return@launch
            }
            when (val result = companyProfileRepository.getByAccountId(accountId)) {
                is Result.Success -> {
                    val profile = result.data
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                id = profile.id,
                                userId = profile.userId,
                                companyName = profile.companyName,
                                logoUrl = profile.logoUrl,
                                website = profile.website,
                                description = profile.description,
                                bio = profile.bio,
                                location = profile.location,
                                industry = profile.industry,
                                twitterUrl = profile.twitterUrl,
                                subscriptionStatus = profile.subscriptionStatus,
                                subscriptionExpiry = profile.subscriptionExpiry,
                                createdAt = profile.createdAt,
                                rating = profile.rating,
                                isVerified = profile.isVerified,
                                isLoading = false,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = UiText.DynamicString("Company profile not found"))
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun uploadImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, error = null) }
            when (val result = accountRepository.uploadImage(imageBytes, "logo-${System.currentTimeMillis()}.jpg")) {
                is Result.Success -> {
                    _uiState.update { it.copy(logoUrl = result.data, isUploadingImage = false) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isUploadingImage = false) }
                }
            }
        }
    }

    private fun save(onSave: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val s = _uiState.value
            val result = companyProfileRepository.update(
                CompanyProfile(
                    id = s.id,
                    userId = s.userId,
                    companyName = s.companyName,
                    logoUrl = s.logoUrl,
                    website = s.website,
                    description = s.description,
                    subscriptionStatus = s.subscriptionStatus,
                    subscriptionExpiry = s.subscriptionExpiry,
                    createdAt = s.createdAt,
                    bio = s.bio,
                    location = s.location,
                    industry = s.industry,
                    twitterUrl = s.twitterUrl,
                    rating = s.rating,
                    isVerified = s.isVerified,
                )
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSave()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isSaving = false) }
                }
            }
        }
    }
}
