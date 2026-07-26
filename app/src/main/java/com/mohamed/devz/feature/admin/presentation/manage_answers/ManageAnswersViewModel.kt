package com.mohamed.devz.feature.admin.presentation.manage_answers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAnswersViewModel @Inject constructor(
    private val answerRepository: AnswerRepository,
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAnswersState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnswers()
    }

    fun onAction(action: ManageAnswersAction) {
        when (action) {
            is ManageAnswersAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
                applyFilters()
            }
            is ManageAnswersAction.DeleteAnswer -> {
                _uiState.update {
                    it.copy(showDeleteDialog = true, targetDeleteAnswer = action.answer)
                }
            }
            is ManageAnswersAction.ConfirmDelete -> {
                deleteAnswer(action.answer)
            }
            is ManageAnswersAction.DismissDeleteDialog -> {
                _uiState.update {
                    it.copy(showDeleteDialog = false, targetDeleteAnswer = null)
                }
            }
            is ManageAnswersAction.Refresh -> loadAnswers()
        }
    }

    private fun loadAnswers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = answerRepository.getAll()) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }

                    val authorNames = mutableMapOf<Int, String>()
                    val questionTitles = mutableMapOf<Int, String>()

                    val uniqueAccountIds = sorted.map { it.accountId }.distinct()
                    val uniqueQuestionIds = sorted.map { it.questionId }.distinct()

                    for (accountId in uniqueAccountIds) {
                        when (val accResult = accountRepository.getById(accountId)) {
                            is Result.Success -> {
                                authorNames[accountId] = accResult.data.fullName
                            }
                            is Result.Error -> {}
                        }
                    }

                    for (questionId in uniqueQuestionIds) {
                        when (val qResult = questionRepository.getById(questionId)) {
                            is Result.Success -> {
                                questionTitles[questionId] = qResult.data.title
                            }
                            is Result.Error -> {}
                        }
                    }

                    _uiState.update {
                        it.copy(
                            allAnswers = sorted,
                            authorNames = authorNames,
                            questionTitles = questionTitles,
                        )
                    }
                    applyFilters()
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUIText(),
                        )
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.allAnswers

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.description.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredAnswers = filtered) }
    }

    private fun deleteAnswer(answer: Answer) {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = false, targetDeleteAnswer = null) }

            when (val result = answerRepository.delete(answer)) {
                is Result.Success -> {
                    when (val qResult = questionRepository.getById(answer.questionId)) {
                        is Result.Success -> {
                            questionRepository.decrementAnswerCount(answer.questionId, qResult.data.answersCount)
                        }
                        is Result.Error -> {}
                    }
                    loadAnswers()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
