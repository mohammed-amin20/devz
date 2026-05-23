package com.mohamed.devz.feature.profile.presentation.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
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
class EditProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.FullNameChanged -> _uiState.update { it.copy(fullName = action.v) }
            is EditProfileAction.UsernameChanged -> _uiState.update { it.copy(username = action.v) }
            is EditProfileAction.BioChanged -> _uiState.update { it.copy(bio = action.v) }
            is EditProfileAction.GithubChanged -> _uiState.update { it.copy(github = action.v) }
            is EditProfileAction.LinkedinChanged -> _uiState.update { it.copy(linkedin = action.v) }
            is EditProfileAction.WebsiteChanged -> _uiState.update { it.copy(website = action.v) }
            is EditProfileAction.RemoveSkill -> _uiState.update { it.copy(skills = it.skills - action.skill) }
            is EditProfileAction.SkillInputChanged -> _uiState.update { it.copy(skillInput = action.v) }
            is EditProfileAction.ShowSkillInput -> _uiState.update { it.copy(showSkillInput = true) }
            is EditProfileAction.AddSkill -> {
                val skill = _uiState.value.skillInput.trim()
                if (skill.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            skills = it.skills + skill,
                            skillInput = "",
                            showSkillInput = false
                        )
                    }
                }
            }
            is EditProfileAction.TogglePublicProfile -> _uiState.update { it.copy(isPublicProfile = !it.isPublicProfile) }
            is EditProfileAction.ToggleDisplayEmail -> _uiState.update { it.copy(displayEmail = !it.displayEmail) }
            is EditProfileAction.PickImage -> uploadImage(action.imageBytes)
            is EditProfileAction.Save -> save(action.onSave)
            is EditProfileAction.DeactivateAccount -> { }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("User not found")) }
                return@launch
            }
            when (val result = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val a = result.data
                    _uiState.update {
                        it.copy(
                            id = a.id,
                            fullName = a.fullName,
                            username = a.username,
                            email = a.email,
                            password = a.password,
                            bio = a.bio,
                            github = a.githubUrl,
                            linkedin = a.linkedInUrl,
                            website = a.websiteUrl,
                            skills = a.techStack.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() },
                            imageUrl = a.imageUrl,
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

    private fun uploadImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = accountRepository.uploadImage(imageBytes, "image-${System.currentTimeMillis()}.jpg")) {
                is Result.Success -> {
                    _uiState.update { it.copy(imageUrl = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }

    private fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val result = accountRepository.update(
                Account(
                    id = state.id,
                    username = state.username,
                    fullName = state.fullName,
                    email = state.email,
                    password = state.password,
                    imageUrl = state.imageUrl,
                    bio = state.bio,
                    techStack = state.skills.joinToString(", "),
                    githubUrl = state.github,
                    linkedInUrl = state.linkedin,
                    websiteUrl = state.website,
                )
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSaved()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }
}
