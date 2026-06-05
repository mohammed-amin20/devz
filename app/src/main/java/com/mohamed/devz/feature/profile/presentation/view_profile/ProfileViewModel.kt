package com.mohamed.devz.feature.profile.presentation.view_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileAnswerUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileQuestionUiModel
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.Refresh -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("User not found")) }
                return@launch
            }

            when (val accountResult = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val account = accountResult.data

                    val questionsResult = questionRepository.getByAccountId(accountId)
                    val answersResult = answerRepository.getByAccountId(accountId)

                    val questions = (questionsResult as? Result.Success)?.data ?: emptyList()
                    val answers = (answersResult as? Result.Success)?.data ?: emptyList()

                    val acceptedAnswers = answers.count { it.accepted }
                    val acceptedRate = if (answers.isNotEmpty()) {
                        "${acceptedAnswers * 100f / answers.size}%"
                    } else "0%"

                    val questionMap = questions.associateBy { it.id }

                    _uiState.update {
                        it.copy(
                            id = account.id,
                            profile = ProfileUiModel(
                                fullName = account.fullName,
                                username = account.username,
                                points = "0",
                                answerCount = answers.size,
                                questionCount = questions.size,
                                acceptedRate = acceptedRate,
                                globalRank = "-",
                                skills = account.techStack.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
                            ),
                            myQuestions = questions.map { q ->
                                ProfileQuestionUiModel(
                                    id = q.id,
                                    title = q.title,
                                    timeAgo = q.createdAt ?: "",
                                    votes = q.likesCount,
                                    answerCount = q.answersCount,
                                    tags = q.tags.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
                                )
                            },
                            myAnswers = answers.map { a ->
                                ProfileAnswerUiModel(
                                    id = a.id,
                                    questionTitle = questionMap[a.questionId]?.title ?: "",
                                    preview = a.description,
                                    likes = a.votedIds.split(",").count { id -> id.isNotBlank() },
                                    comments = 0,
                                    timeAgo = a.createdAt ?: "",
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