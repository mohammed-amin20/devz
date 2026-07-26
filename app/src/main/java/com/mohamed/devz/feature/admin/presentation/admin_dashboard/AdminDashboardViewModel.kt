package com.mohamed.devz.feature.admin.presentation.admin_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun refresh() = loadStats()

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val accountsDeferred = async { accountRepository.getAll() }
            val questionsDeferred = async { questionRepository.getAll(0, 999999) }
            val answersDeferred = async { answerRepository.getAll() }

            val accountsResult = accountsDeferred.await()
            val questionsResult = questionsDeferred.await()
            val answersResult = answersDeferred.await()

            val accounts = (accountsResult as? Result.Success)?.data ?: emptyList()
            val questions = (questionsResult as? Result.Success)?.data ?: emptyList()
            val answers = (answersResult as? Result.Success)?.data ?: emptyList()

            val errors = listOfNotNull(
                (accountsResult as? Result.Error)?.error,
                (questionsResult as? Result.Error)?.error,
                (answersResult as? Result.Error)?.error,
            )

            _uiState.update {
                it.copy(
                    totalUsers = accounts.size,
                    totalQuestions = questions.size,
                    totalAnswers = answers.size,
                    bannedUsers = accounts.count { a -> a.isBanned },
                    isLoading = false,
                    error = errors.firstOrNull()?.toUIText(),
                )
            }
        }
    }
}
