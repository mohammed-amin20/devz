package com.mohamed.devz.feature.profile.presentation.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: EditProfileAction, onSaved: () -> Unit = {}) {
        when (action) {
            is EditProfileAction.FullNameChanged   -> _uiState.update { it.copy(fullName = action.v) }
            is EditProfileAction.UsernameChanged   -> _uiState.update { it.copy(username = action.v) }
            is EditProfileAction.BioChanged        -> _uiState.update { it.copy(bio = action.v) }
            is EditProfileAction.GithubChanged     -> _uiState.update { it.copy(github = action.v) }
            is EditProfileAction.LinkedinChanged   -> _uiState.update { it.copy(linkedin = action.v) }
            is EditProfileAction.WebsiteChanged    -> _uiState.update { it.copy(website = action.v) }
            is EditProfileAction.RemoveSkill       -> _uiState.update { it.copy(skills = it.skills - action.skill) }
            is EditProfileAction.SkillInputChanged -> _uiState.update { it.copy(skillInput = action.v) }
            is EditProfileAction.ShowSkillInput    -> _uiState.update { it.copy(showSkillInput = true) }
            is EditProfileAction.AddSkill          -> {
                val skill = _uiState.value.skillInput.trim()
                if (skill.isNotEmpty()) {
                    _uiState.update { it.copy(
                        skills = it.skills + skill,
                        skillInput = "",
                        showSkillInput = false
                    )}
                }
            }
            is EditProfileAction.TogglePublicProfile -> _uiState.update { it.copy(isPublicProfile = !it.isPublicProfile) }
            is EditProfileAction.ToggleDisplayEmail  -> _uiState.update { it.copy(displayEmail = !it.displayEmail) }
            is EditProfileAction.Save -> save(onSaved)
            is EditProfileAction.PickAvatar -> { /* TODO: image picker */ }
            is EditProfileAction.DeactivateAccount -> { /* TODO: show confirmation dialog */ }
        }
    }

    private fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: repository.updateProfile(...)
            _uiState.update { it.copy(isLoading = false) }
            onSaved()
        }
    }
}