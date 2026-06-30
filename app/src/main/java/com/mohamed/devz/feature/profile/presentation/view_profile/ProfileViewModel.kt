package com.mohamed.devz.feature.profile.presentation.view_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileAnswerUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileQuestionUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileEvent {
    data object NavigateToAuth : ProfileEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _profileEvent = MutableSharedFlow<ProfileEvent>()
    val profileEvent: SharedFlow<ProfileEvent> = _profileEvent.asSharedFlow()

    init {
        val targetAccountId = savedStateHandle.get<Int>("accountId")
        loadProfile(targetAccountId)
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.Refresh -> {
                val targetAccountId = savedStateHandle.get<Int>("accountId")
                loadProfile(targetAccountId)
            }
            is ProfileAction.Logout -> logout()
            is ProfileAction.ToggleFollow -> toggleFollow(action.targetAccountId)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.setLoggedOut()
            userPreferencesRepository.clearAccountId()
            _profileEvent.emit(ProfileEvent.NavigateToAuth)
        }
    }

    private fun toggleFollow(targetAccountId: Int) {
        viewModelScope.launch {
            val currentId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (currentId == 0 || targetAccountId == currentId) return@launch

            val currentState = _uiState.value
            val newIsFollowing = !currentState.isFollowing

            _uiState.update {
                it.copy(
                    isFollowing = newIsFollowing,
                    followersCount = if (newIsFollowing) it.followersCount + 1 else (it.followersCount - 1).coerceAtLeast(0),
                )
            }

            val result = if (newIsFollowing) {
                accountRepository.follow(currentId, targetAccountId)
            } else {
                accountRepository.unfollow(currentId, targetAccountId)
            }

            if (result is Result.Error) {
                _uiState.update {
                    it.copy(
                        error = result.error.toUIText(),
                        isFollowing = currentState.isFollowing,
                        followersCount = currentState.followersCount,
                    )
                }
            }
        }
    }

    private fun loadProfile(targetAccountId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val loggedInAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            val accountId = targetAccountId ?: loggedInAccountId
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("User not found")) }
                return@launch
            }

            when (val accountResult = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val account = accountResult.data

                    val questionsResult = questionRepository.getByAccountId(accountId)
                    val answersResult = answerRepository.getByAccountId(accountId)

                    val questions = ((questionsResult as? Result.Success)?.data ?: emptyList())
                        .sortedByDescending { it.createdAt }
                    val answers = ((answersResult as? Result.Success)?.data ?: emptyList())
                        .sortedByDescending { it.createdAt }

                    val acceptedAnswers = answers.count { it.accepted }
                    val acceptedRate = if (answers.isNotEmpty()) {
                        "${(acceptedAnswers * 100f / answers.size).toInt()}%"
                    } else "0%"

                    val questionMap = questions.associateBy { it.id }
                    val isOwnProfile = targetAccountId == null || account.id == loggedInAccountId

                    val followersCount = account.followerIds
                        .split(",").count { it.isNotBlank() }
                    val followingCount = account.followingIds
                        .split(",").count { it.isNotBlank() }
                    val isFollowing = !isOwnProfile && loggedInAccountId != 0 &&
                        account.followerIds.split(",").any { it.trim() == loggedInAccountId.toString() }

                    _uiState.update {
                        it.copy(
                            id = account.id,
                            isOwnProfile = isOwnProfile,
                            followersCount = followersCount,
                            followingCount = followingCount,
                            isFollowing = isFollowing,
                            profile = ProfileUiModel(
                                fullName = account.fullName,
                                username = account.username,
                                imageUrl = account.imageUrl,
                                bio = account.bio,
                                points = account.points.toString(),
                                answerCount = answers.size,
                                questionCount = questions.size,
                                acceptedRate = acceptedRate,
                                skills = account.techStack.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() },
                                githubUrl = account.githubUrl,
                                linkedInUrl = account.linkedInUrl,
                                websiteUrl = account.websiteUrl,
                                followersCount = followersCount,
                                followingCount = followingCount,
                            ),
                            myQuestions = questions.map { q ->
                                ProfileQuestionUiModel(
                                    id = q.id,
                                    title = q.title,
                                    timeAgo = formatRelativeTime(q.createdAt),
                                    votes = q.likesCount,
                                    answerCount = q.answersCount,
                                    tags = q.tags.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
                                )
                            },
                            myAnswers = answers.map { a ->
                                ProfileAnswerUiModel(
                                    id = a.id,
                                    questionId = a.questionId,
                                    questionTitle = questionMap[a.questionId]?.title ?: "",
                                    preview = a.description,
                                    likes = a.votedIds.split(",").count { id -> id.isNotBlank() },
                                    timeAgo = formatRelativeTime(a.createdAt),
                                    isAccepted = a.accepted
                                )
                            },
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = accountResult.error.toUIText(), isLoading = false) }
                }
            }
        }
    }
}