package com.mohamed.devz.feature.profile.presentation.view_profile.components

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


// ── ProfileUiState ────────────────────────────────────────────────────────────
data class ProfileUiModel(
    val fullName: String,
    val username: String,
    val points: String,
    val answerCount: Int,
    val questionCount: Int,
    val acceptedRate: String,
    val globalRank: String,
    val skills: List<String>
)

data class ProfileQuestionUiModel(
    val id: String,
    val title: String,
    val timeAgo: String,
    val votes: Int,
    val answerCount: Int,
    val tags: List<String>
)

data class ProfileAnswerUiModel(
    val id: String,
    val questionTitle: String,
    val preview: String,
    val likes: Int,
    val comments: Int,
    val timeAgo: String,
    val isAccepted: Boolean
)

data class ProfileUiState(
    val id: String = "",
    val profile: ProfileUiModel? = null,
    val myQuestions: List<ProfileQuestionUiModel> = emptyList(),
    val myAnswers: List<ProfileAnswerUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ── ProfileViewModel ──────────────────────────────────────────────────────────
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
}


