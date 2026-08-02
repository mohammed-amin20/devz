package com.mohamed.devz.feature.question.presentation.question_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.model.toggleVote
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerUiModel
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
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

@HiltViewModel
class QuestionDetailsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationRepository: NotificationRepository,
    private val fcmPushSender: FcmPushSender,
) : ViewModel() {

    sealed interface QuestionDetailsEvent {
        data class ShowError(val message: String) : QuestionDetailsEvent
    }

    private val _uiState = MutableStateFlow(QuestionDetailsState())
    val uiState = _uiState.asStateFlow()

    private val _questionDetailsEvent = MutableSharedFlow<QuestionDetailsEvent>()
    val questionDetailsEvent: SharedFlow<QuestionDetailsEvent> =
        _questionDetailsEvent.asSharedFlow()

    private var currentQuestionId: Int? = null
    private var currentAccountId: Int = 0
    private var questionOwnerAccountId: Int = 0

    init {
        viewModelScope.launch {
            currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            _uiState.update { it.copy(currentAccountId = currentAccountId) }
        }
    }

    fun onAction(action: QuestionDetailsAction) {
        when (action) {
            is QuestionDetailsAction.LoadQuestion -> loadQuestion(action.questionId)
            is QuestionDetailsAction.AnswerTextChanged -> _uiState.update { it.copy(answerText = action.value) }
            is QuestionDetailsAction.AnswerCodeChanged -> _uiState.update { it.copy(answerCode = action.value) }
            is QuestionDetailsAction.ShowCodeEditor -> _uiState.update { it.copy(showCodeEditor = true) }
            is QuestionDetailsAction.HideCodeEditor -> _uiState.update { it.copy(showCodeEditor = false) }
            is QuestionDetailsAction.PrefillAnswerCode -> _uiState.update { it.copy(answerCode = action.code, showCodeEditor = true) }
            is QuestionDetailsAction.PostAnswer -> postAnswer(action.onSuccess)
            is QuestionDetailsAction.ToggleLike -> toggleLike()
            is QuestionDetailsAction.ToggleAnswerVote -> toggleAnswerVote(action.answerId)
            is QuestionDetailsAction.AcceptAnswer -> acceptAnswer(action.answerId)
            is QuestionDetailsAction.PinQuestion -> pinQuestion()
            is QuestionDetailsAction.ShowReport -> {
                _uiState.update { it.copy(reportTarget = action.target) }
            }
            is QuestionDetailsAction.DismissReport -> {
                _uiState.update { it.copy(reportTarget = null) }
            }
        }
    }

    private fun toggleLike() {
        val state = _uiState.value
        val question = state.question ?: return
        val questionId = currentQuestionId ?: return
        if (currentAccountId == 0) return
        val wasLiked = question.isLiked
        val originalLikes = question.likes
        val originalLikedIds = question.likedAccountIds

        viewModelScope.launch {
            _uiState.update { it.copy(isLiking = true) }
            val currentLikedIds = if (wasLiked) {
                originalLikedIds
                    .split(",")
                    .filter { it.isNotBlank() && it.toIntOrNull() != currentAccountId }
                    .joinToString(",")
            } else {
                val ids = originalLikedIds.split(",").filter { it.isNotBlank() }.toMutableList()
                ids.add(currentAccountId.toString())
                ids.joinToString(",")
            }
            val newCount = if (currentLikedIds.isBlank()) 0 else currentLikedIds.split(",").size
            _uiState.update {
                it.copy(
                    question = it.question?.copy(
                        likes = newCount,
                        isLiked = !wasLiked,
                        likedAccountIds = currentLikedIds,
                    ),
                    isLiking = false,
                )
            }
            when (questionRepository.toggleLike(questionId, currentLikedIds, newCount)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            question = it.question?.copy(
                                likes = originalLikes,
                                isLiked = wasLiked,
                                likedAccountIds = originalLikedIds,
                            ),
                        )
                    }
                }

                is Result.Success -> {
                    if (!wasLiked && currentAccountId != questionOwnerAccountId) {
                        notificationRepository.insert(
                            Notification(
                                id = 0,
                                typeId = 2,
                                userId = questionOwnerAccountId,
                                actorId = currentAccountId,
                                questionId = questionId,
                                answerId = null,
                                type = "like",
                                message = "liked your question",
                                isRead = false,
                                createdAt = "",
                            )
                        )
                        kotlin.runCatching {
                            val actor = (accountRepository.getById(currentAccountId) as? Result.Success)?.data
                            val recipient = (accountRepository.getById(questionOwnerAccountId) as? Result.Success)?.data
                            if (actor != null && recipient != null && recipient.fcmToken.isNotBlank()) {
                                fcmPushSender.sendPush(
                                    fcmToken = recipient.fcmToken,
                                    title = "New like",
                                    body = "${actor.fullName} liked your question",
                                    questionId = questionId,
                                    type = "like",
                                )
                            }
                        }
                    }
                    if (currentAccountId != questionOwnerAccountId) {
                        accountRepository.addPoints(
                            questionOwnerAccountId,
                            if (!wasLiked) 1 else -1
                        )
                    }
                }
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
                    questionOwnerAccountId = q.accountId
                    val account = (accountRepository.getById(q.accountId) as? Result.Success)?.data
                    val currentAccount = (accountRepository.getById(currentAccountId) as? Result.Success)?.data
                    val langTypeId = q.langTypeId
                    val isLiked = q.likedAccountIds
                        .split(",")
                        .any { it.trim().toIntOrNull() == currentAccountId }
                    val isPinned = q.pinnedUntil != null && q.pinnedUntil > "2020-01-01"

                    val detailUiModel = QuestionDetailUiModel(
                        title = q.title,
                        authorName = account?.fullName ?: "Unknown",
                        authorAvatarUrl = account?.imageUrl ?: "",
                        authorAccountId = q.accountId,
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
                        isPinned = isPinned,
                        isAuthorPro = account?.isPro ?: false,
                    )
                    _uiState.update {
                        it.copy(
                            question = detailUiModel,
                            isLoading = false,
                            error = null,
                            isPro = currentAccount?.isPro ?: false,
                            isCompany = currentAccount?.accountType == "company",
                        )
                    }
                    loadAnswers(questionId)
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = questionResult.error.toUIText(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadAnswers(questionId: Int) {
        when (val result = answerRepository.getByQuestionId(questionId)) {
            is Result.Success -> {
                val answers = result.data.sortedByDescending { it.createdAt }
                val answerUiModels = answers.map { answer ->
                    val votes = answer.votedIds.split(",").filter { it.isNotBlank() }
                    val author =
                        (accountRepository.getById(answer.accountId) as? Result.Success)?.data
                    AnswerUiModel(
                        answerId = answer.id,
                        authorName = author?.fullName ?: "Unknown",
                        avatarUrl = author?.imageUrl ?: "",
                        authorAccountId = answer.accountId,
                        isAuthorPro = author?.isPro ?: false,
                        body = answer.description,
                        isAccepted = answer.accepted,
                        likes = votes.size,
                        isLiked = if (currentAccountId != 0) votes.any { it == currentAccountId.toString() } else false,
                        timeAgo = formatRelativeTime(answer.createdAt),
                        code = answer.code,
                        language = _uiState.value.question?.language ?: SyntaxLanguage.GENERIC,
                    )
                }
                _uiState.update { it.copy(answers = answerUiModels) }
            }

            is Result.Error -> {
                _questionDetailsEvent.emit(
                    QuestionDetailsEvent.ShowError(
                        (result.error.toUIText() as? UiText.DynamicString)?.value
                            ?: "An error occurred"
                    )
                )
            }
        }
    }

    private fun toggleAnswerVote(answerId: Int) {
        if (currentAccountId == 0) return

        val answers = _uiState.value.answers
        val index = answers.indexOfFirst { it.answerId == answerId }
        if (index == -1) return
        val original = answers[index]

        _uiState.update { state ->
            val idx = state.answers.indexOfFirst { it.answerId == answerId }
            if (idx == -1) return@update state
            val current = state.answers[idx]
            state.copy(answers = state.answers.toMutableList().apply {
                set(
                    idx, current.copy(
                        likes = if (current.isLiked) current.likes - 1 else current.likes + 1,
                        isLiked = !current.isLiked
                    )
                )
            })
        }

        viewModelScope.launch {
            when (val answer = answerRepository.getById(answerId)) {
                is Result.Success -> {
                    when (val result =
                        answerRepository.update(answer.data.toggleVote(currentAccountId))) {
                        is Result.Success -> {
                            if (currentAccountId != original.authorAccountId) {
                                kotlin.runCatching {
                                    accountRepository.addPoints(
                                        original.authorAccountId,
                                        if (!original.isLiked) 1 else -1
                                    )
                                }
                            }
                            if (!original.isLiked && currentAccountId != original.authorAccountId) {
                                notificationRepository.insert(
                                    Notification(
                                        id = 0,
                                        typeId = 4,
                                        userId = original.authorAccountId,
                                        actorId = currentAccountId,
                                        questionId = currentQuestionId ?: return@launch,
                                        answerId = answerId,
                                        type = "upvote",
                                        message = "upvoted your answer",
                                        isRead = false,
                                        createdAt = "",
                                    )
                                )
                                kotlin.runCatching {
                                    val actor = (accountRepository.getById(currentAccountId) as? Result.Success)?.data
                                    val recipient = (accountRepository.getById(original.authorAccountId) as? Result.Success)?.data
                                    if (actor != null && recipient != null && recipient.fcmToken.isNotBlank()) {
                                        fcmPushSender.sendPush(
                                            fcmToken = recipient.fcmToken,
                                            title = "New upvote",
                                            body = "${actor.fullName} upvoted your answer",
                                            questionId = currentQuestionId ?: return@runCatching,
                                            type = "upvote",
                                        )
                                    }
                                }
                            }
                        }

                        is Result.Error -> {
                            rollbackAnswerVote(answerId, original)
                            _questionDetailsEvent.emit(
                                QuestionDetailsEvent.ShowError(
                                    (result.error.toUIText() as? UiText.DynamicString)?.value
                                        ?: "An error occurred"
                                )
                            )
                        }
                    }
                }

                is Result.Error -> {
                    rollbackAnswerVote(answerId, original)
                    _questionDetailsEvent.emit(
                        QuestionDetailsEvent.ShowError(
                            (answer.error.toUIText() as? UiText.DynamicString)?.value
                                ?: "An error occurred"
                        )
                    )
                }
            }
        }
    }

    private fun rollbackAnswerVote(answerId: Int, original: AnswerUiModel) {
        _uiState.update { state ->
            val idx = state.answers.indexOfFirst { it.answerId == answerId }
            if (idx == -1) return@update state
            state.copy(answers = state.answers.toMutableList().apply { set(idx, original) })
        }
    }

    private fun postAnswer(onSuccess: () -> Unit) {
        if (_uiState.value.isCompany) return
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
                code = _uiState.value.answerCode,
            )
            when (val result = answerRepository.insert(answer)) {
                is Result.Success -> {
                    val insertedAnswer = result.data
                    val currentCount = _uiState.value.question?.answersCount ?: 0
                    _uiState.update { it.copy(answerText = "", answerCode = null, showCodeEditor = false, isPosting = false) }
                    questionRepository.incrementAnswerCount(questionId, currentCount)
                    _uiState.update {
                        it.copy(question = it.question?.copy(answersCount = currentCount + 1))
                    }
                    loadAnswers(questionId)
                    if (currentAccountId != questionOwnerAccountId) {
                        notificationRepository.insert(
                            Notification(
                                id = 0,
                                typeId = 3,
                                userId = questionOwnerAccountId,
                                actorId = currentAccountId,
                                questionId = questionId,
                                answerId = insertedAnswer.id,
                                type = "answer",
                                message = "answered your question",
                                isRead = false,
                                createdAt = "",
                            )
                        )
                        kotlin.runCatching {
                            val actor = (accountRepository.getById(currentAccountId) as? Result.Success)?.data
                            val recipient = (accountRepository.getById(questionOwnerAccountId) as? Result.Success)?.data
                            if (actor != null && recipient != null && recipient.fcmToken.isNotBlank()) {
                                fcmPushSender.sendPush(
                                    fcmToken = recipient.fcmToken,
                                    title = "New answer",
                                    body = "${actor.fullName} answered your question",
                                    questionId = questionId,
                                    type = "answer",
                                )
                            }
                        }
                        accountRepository.addPoints(questionOwnerAccountId, 1)
                    }
                    onSuccess()
                }

                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isPosting = false) }
                }
            }
        }
    }

    private fun acceptAnswer(answerId: Int) {
        if (currentAccountId == 0 || currentAccountId != questionOwnerAccountId) return

        val originalAnswers = _uiState.value.answers
        val newAnswerIndex = originalAnswers.indexOfFirst { it.answerId == answerId }
        if (newAnswerIndex == -1) return
        if (originalAnswers[newAnswerIndex].isAccepted) return

        val prevAcceptedIndex = originalAnswers.indexOfFirst { it.isAccepted }

        _uiState.update { state ->
            val mutableAnswers = state.answers.toMutableList()
            if (prevAcceptedIndex != -1)
                mutableAnswers[prevAcceptedIndex] =
                    mutableAnswers[prevAcceptedIndex].copy(isAccepted = false)
            mutableAnswers[newAnswerIndex] = mutableAnswers[newAnswerIndex].copy(isAccepted = true)
            state.copy(answers = mutableAnswers)
        }

        viewModelScope.launch {
            if (prevAcceptedIndex != -1) {
                val oldAnswerId = originalAnswers[prevAcceptedIndex].answerId
                when (val oldResult = answerRepository.getById(oldAnswerId)) {
                    is Result.Success -> {
                        when (val result =
                            answerRepository.update(oldResult.data.copy(accepted = false))) {
                            is Result.Error -> {
                                rollbackAcceptAnswer(originalAnswers)
                                _questionDetailsEvent.emit(
                                    QuestionDetailsEvent.ShowError(
                                        (result.error.toUIText() as? UiText.DynamicString)?.value
                                            ?: "An error occurred"
                                    )
                                )
                                return@launch
                            }

                            is Result.Success -> Unit
                        }
                    }

                    is Result.Error -> {
                        rollbackAcceptAnswer(originalAnswers)
                        _questionDetailsEvent.emit(
                            QuestionDetailsEvent.ShowError(
                                (oldResult.error.toUIText() as? UiText.DynamicString)?.value
                                    ?: "An error occurred"
                            )
                        )
                        return@launch
                    }
                }
            }

            when (val newResult = answerRepository.getById(answerId)) {
                is Result.Error -> {
                    if (prevAcceptedIndex != -1) {
                        val res =
                            answerRepository.getById(originalAnswers[prevAcceptedIndex].answerId)
                        if (res is Result.Success) answerRepository.update(res.data.copy(accepted = true))
                    }
                    rollbackAcceptAnswer(originalAnswers)
                    _questionDetailsEvent.emit(
                        QuestionDetailsEvent.ShowError(
                            (newResult.error.toUIText() as? UiText.DynamicString)?.value
                                ?: "An error occurred"
                        )
                    )
                }

                is Result.Success -> {
                    when (val result =
                        answerRepository.update(newResult.data.copy(accepted = true))) {
                        is Result.Error -> {
                            if (prevAcceptedIndex != -1) {
                                val res =
                                    answerRepository.getById(originalAnswers[prevAcceptedIndex].answerId)
                                if (res is Result.Success) answerRepository.update(
                                    res.data.copy(
                                        accepted = true
                                    )
                                )
                            }
                            rollbackAcceptAnswer(originalAnswers)
                            _questionDetailsEvent.emit(
                                QuestionDetailsEvent.ShowError(
                                    (result.error.toUIText() as? UiText.DynamicString)?.value
                                        ?: "An error occurred"
                                )
                            )
                        }

                        is Result.Success -> {
                            val newAnswerAuthorId = originalAnswers[newAnswerIndex].authorAccountId
                            if (currentAccountId != newAnswerAuthorId) {
                                when (val pointsResult = accountRepository.addPoints(newAnswerAuthorId, 3)) {
                                    is Result.Error -> {
                                        val message = (pointsResult.error.toUIText() as? UiText.DynamicString)?.value
                                            ?: "An error occurred"
                                        _questionDetailsEvent.emit(QuestionDetailsEvent.ShowError(message))
                                    }
                                    is Result.Success -> Unit
                                }
                            }
                            if (currentAccountId != newAnswerAuthorId) {
                                notificationRepository.insert(
                                    Notification(
                                        id = 0,
                                        typeId = 1,
                                        userId = newAnswerAuthorId,
                                        actorId = currentAccountId,
                                        questionId = currentQuestionId ?: return@launch,
                                        answerId = answerId,
                                        type = "accepted",
                                        message = "accepted your answer",
                                        isRead = false,
                                        createdAt = "",
                                    )
                                )
                                kotlin.runCatching {
                                    val actor = (accountRepository.getById(currentAccountId) as? Result.Success)?.data
                                    val recipient = (accountRepository.getById(newAnswerAuthorId) as? Result.Success)?.data
                                    if (actor != null && recipient != null && recipient.fcmToken.isNotBlank()) {
                                        fcmPushSender.sendPush(
                                            fcmToken = recipient.fcmToken,
                                            title = "Answer accepted",
                                            body = "${actor.fullName} accepted your answer",
                                            questionId = currentQuestionId ?: return@runCatching,
                                            type = "accepted",
                                        )
                                    }
                                }
                            }
                            if (prevAcceptedIndex != -1) {
                                val oldAnswerAuthorId =
                                    originalAnswers[prevAcceptedIndex].authorAccountId
                                if (currentAccountId != oldAnswerAuthorId) {
                                    when (val pointsResult = accountRepository.addPoints(oldAnswerAuthorId, -3)) {
                                        is Result.Error -> {
                                            val message = (pointsResult.error.toUIText() as? UiText.DynamicString)?.value
                                                ?: "An error occurred"
                                            _questionDetailsEvent.emit(QuestionDetailsEvent.ShowError(message))
                                        }
                                        is Result.Success -> Unit
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun pinQuestion() {
        val questionId = currentQuestionId ?: return
        val isCurrentlyPinned = _uiState.value.question?.isPinned ?: false

        viewModelScope.launch {
            _uiState.update { it.copy(isPinning = true) }

            if (isCurrentlyPinned) {
                when (val result = questionRepository.getById(questionId)) {
                    is Result.Success -> {
                        val updatedQuestion = result.data.copy(pinnedUntil = null)
                        when (questionRepository.update(updatedQuestion)) {
                            is Result.Success -> {
                                _uiState.update {
                                    it.copy(
                                        question = it.question?.copy(isPinned = false),
                                        isPinning = false,
                                    )
                                }
                            }
                            is Result.Error -> {
                                _uiState.update { it.copy(isPinning = false) }
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isPinning = false) }
                    }
                }
            } else {
                when (val pinnedResult = questionRepository.getPinnedQuestions()) {
                    is Result.Success -> {
                        val now = java.time.LocalDateTime.now().toString()
                        val existingPin = pinnedResult.data
                            .filter { it.pinnedUntil != null && it.pinnedUntil >= now }
                            .find { it.accountId == currentAccountId }
                        if (existingPin != null) {
                            questionRepository.update(existingPin.copy(pinnedUntil = null))
                        }
                        val newPinnedUntil = java.time.LocalDateTime.now().plusHours(24).toString()

                        when (val result = questionRepository.getById(questionId)) {
                            is Result.Success -> {
                                val updatedQuestion = result.data.copy(pinnedUntil = newPinnedUntil)
                                when (questionRepository.update(updatedQuestion)) {
                                    is Result.Success -> {
                                        _uiState.update {
                                            it.copy(
                                                question = it.question?.copy(isPinned = true),
                                                isPinning = false,
                                            )
                                        }
                                    }
                                    is Result.Error -> {
                                        _uiState.update { it.copy(isPinning = false) }
                                    }
                                }
                            }
                            is Result.Error -> {
                                _uiState.update { it.copy(isPinning = false) }
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isPinning = false) }
                    }
                }
            }
        }
    }

    private fun rollbackAcceptAnswer(originalAnswers: List<AnswerUiModel>) {
        _uiState.update { it.copy(answers = originalAnswers) }
    }
}
