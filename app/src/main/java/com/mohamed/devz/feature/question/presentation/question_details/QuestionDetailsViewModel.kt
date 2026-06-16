package com.mohamed.devz.feature.question.presentation.question_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerUiModel
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionDetailsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionDetailsState())
    val uiState = _uiState.asStateFlow()

    private var currentQuestionId: Int? = null
    private var currentAccountId: Int = 0

    init {
        viewModelScope.launch {
            currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
        }
    }

    fun onAction(action: QuestionDetailsAction) {
        when (action) {
            is QuestionDetailsAction.LoadQuestion -> loadQuestion(action.questionId)
            is QuestionDetailsAction.AnswerTextChanged -> _uiState.update { it.copy(answerText = action.value) }
            is QuestionDetailsAction.PostAnswer -> postAnswer(action.onSuccess)
            is QuestionDetailsAction.ToggleLike -> toggleLike()
        }
    }

    private fun toggleLike() {
        val state = _uiState.value
        val question = state.question ?: return
        val questionId = currentQuestionId ?: return
        if (currentAccountId == 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLiking = true) }
            val currentLikedIds = if (question.isLiked) {
                question.likedAccountIds
                    .split(",")
                    .filter { it.isNotBlank() && it.toIntOrNull() != currentAccountId }
                    .joinToString(",")
            } else {
                val ids = question.likedAccountIds.split(",").filter { it.isNotBlank() }.toMutableList()
                ids.add(currentAccountId.toString())
                ids.joinToString(",")
            }
            val newCount = if (currentLikedIds.isBlank()) 0 else currentLikedIds.split(",").size
            _uiState.update {
                it.copy(
                    question = it.question?.copy(
                        likes = newCount,
                        isLiked = !question.isLiked,
                    ),
                    isLiking = false,
                )
            }
            when (questionRepository.toggleLike(questionId, currentLikedIds, newCount)) {
                is Result.Error -> {
                    loadQuestion(questionId)
                }
                is Result.Success -> { }
            }
        }
    }

    private fun loadQuestion(questionId: Int) {
        currentQuestionId = questionId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val questionResult = questionRepository.getById(questionId)) {
                is Result.Success -> {
                    val q = questionResult.data
                    val account = (accountRepository.getById(q.accountId) as? Result.Success)?.data
                    val langTypeId = q.langTypeId
                    val isLiked = q.likedAccountIds
                        .split(",")
                        .any { it.trim().toIntOrNull() == currentAccountId }
                    val detailUiModel = QuestionDetailUiModel(
                        title = q.title,
                        authorName = account?.fullName ?: "Unknown",
                        authorAvatarUrl = account?.imageUrl ?: "",
                        timeAgo = formatRelativeTime(q.createdAt),
                        tags = q.tags.split(",").filter { it.isNotBlank() },
                        body = q.description,
                        language = when (langTypeId) {
                            1 -> SyntaxLanguage.KOTLIN
                            2 -> SyntaxLanguage.JAVASCRIPT
                            3 -> SyntaxLanguage.PYTHON
                            else -> SyntaxLanguage.GENERIC
                        },
                        code = q.code,
                        likes = q.likesCount,
                        answersCount = q.answersCount,
                        isLiked = isLiked,
                        likedAccountIds = q.likedAccountIds,
                    )
                    _uiState.update { it.copy(question = detailUiModel, isLoading = false, error = null ) }
                    loadAnswers(questionId)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = questionResult.error.toUIText(), isLoading = false) }
                }
            }
        }
    }

    private suspend fun loadAnswers(questionId: Int) {
        when (val result = answerRepository.getByQuestionId(questionId)) {
            is Result.Success -> {
                val answerUiModels = result.data.map { answer ->
                    val author = (accountRepository.getById(answer.accountId) as? Result.Success)?.data
                    AnswerUiModel(
                        authorName = author?.fullName ?: "Unknown",
                        avatarUrl = author?.imageUrl ?: "",
                        body = answer.description,
                        isAccepted = answer.accepted,
                        likes = answer.votedIds.split(",").count { it.isNotBlank() },
                        timeAgo = formatRelativeTime(answer.createdAt),
                    )
                }
                _uiState.update { it.copy(answers = answerUiModels) }
            }
            is Result.Error -> { /* silently fail for answers */ }
        }
    }

    private fun postAnswer(onSuccess: () -> Unit) {
        val text = _uiState.value.answerText.trim()
        if (text.isEmpty()) return
        val questionId = currentQuestionId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true, error = null) }
            val currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            val answer = Answer(
                id = 0,
                description = text,
                accepted = false,
                votedIds = "",
                questionId = questionId,
                accountId = currentAccountId,
                createdAt = null,
            )
            when (val result = answerRepository.insert(answer)) {
                is Result.Success -> {
                    val currentCount = _uiState.value.question?.answersCount ?: 0
                    _uiState.update { it.copy(answerText = "", isPosting = false) }
                    questionRepository.incrementAnswerCount(questionId, currentCount)
                    _uiState.update {
                        it.copy(question = it.question?.copy(answersCount = currentCount + 1))
                    }
                    loadAnswers(questionId)
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isPosting = false) }
                }
            }
        }
    }
}
