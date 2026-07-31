package com.mohamed.devz.feature.admin.presentation.manage_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
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
class ManageQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageQuestionsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    fun onAction(action: ManageQuestionsAction) {
        when (action) {
            is ManageQuestionsAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
                applyFilters()
            }
            is ManageQuestionsAction.FilterAll -> {
                _uiState.update { it.copy(selectedFilter = 0) }
                applyFilters()
            }
            is ManageQuestionsAction.FilterHidden -> {
                _uiState.update { it.copy(selectedFilter = 1) }
                applyFilters()
            }
            is ManageQuestionsAction.HideQuestion -> {
                _uiState.update {
                    it.copy(showHideDialog = true, targetHideQuestion = action.question)
                }
            }
            is ManageQuestionsAction.ConfirmHide -> {
                hideQuestion(action.question)
            }
            is ManageQuestionsAction.DismissHideDialog -> {
                _uiState.update {
                    it.copy(showHideDialog = false, targetHideQuestion = null)
                }
            }
            is ManageQuestionsAction.UnhideQuestion -> {
                unhideQuestion(action.question)
            }
            is ManageQuestionsAction.Refresh -> loadQuestions(isRefresh = true)
        }
    }

    private fun loadQuestions(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }

            when (val result = questionRepository.getAll(0, 999999)) {
                is Result.Success -> {
                    val sorted = result.data.sortedByDescending { it.id }
                    val uniqueAccountIds = sorted.map { it.accountId }.distinct()
                    val names = mutableMapOf<Int, String>()
                    for (accountId in uniqueAccountIds) {
                        when (val accountResult = accountRepository.getById(accountId)) {
                            is Result.Success -> {
                                val account = accountResult.data
                                names[accountId] = account.fullName.ifBlank { account.username }
                            }
                            is Result.Error -> { }
                        }
                    }
                    _uiState.update {
                        it.copy(allQuestions = sorted, authorNames = names)
                    }
                    applyFilters()
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error.toUIText(),
                        )
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.allQuestions

        if (state.selectedFilter == 1) {
            filtered = filtered.filter { it.isHidden }
        }

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredQuestions = filtered) }
    }

    private fun hideQuestion(question: Question) {
        viewModelScope.launch {
            _uiState.update { it.copy(showHideDialog = false, targetHideQuestion = null) }

            when (val result = questionRepository.update(question.copy(isHidden = true))) {
                is Result.Success -> loadQuestions()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }

    private fun unhideQuestion(question: Question) {
        viewModelScope.launch {
            when (val result = questionRepository.update(question.copy(isHidden = false))) {
                is Result.Success -> loadQuestions()
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText()) }
                }
            }
        }
    }
}
