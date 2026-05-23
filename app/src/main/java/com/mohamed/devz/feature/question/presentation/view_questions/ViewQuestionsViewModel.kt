package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.question.presentation.view_questions.util.toFeedUiModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateAccountCache
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateLanguageTypeCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 10

@HiltViewModel
class ViewQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val accountRepository: AccountRepository,
    private val languageTypeRepository: LanguageTypeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewQuestionsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCaches()
    }

    fun onAction(action: ViewQuestionsAction) {
        when (action) {
            is ViewQuestionsAction.LoadInitialQuestions -> loadPage(0, isRefresh = false)
            is ViewQuestionsAction.LoadNextPage -> {
                val state = _uiState.value
                if (!state.isLoadingMore && state.hasMore) {
                    loadPage(state.currentPage + 1, isRefresh = false)
                }
            }
            is ViewQuestionsAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
            }
            is ViewQuestionsAction.TabSelected -> {
                _uiState.update { it.copy(selectedTab = action.index, currentPage = 0, questions = emptyList(), hasMore = true) }
                loadPage(0, isRefresh = false)
            }
            is ViewQuestionsAction.ToggleBookmark -> {
                _uiState.update {
                    val updated = it.bookmarkedIds.toMutableSet()
                    if (action.questionId in updated) updated.remove(action.questionId)
                    else updated.add(action.questionId)
                    it.copy(bookmarkedIds = updated)
                }
            }
            is ViewQuestionsAction.Refresh -> loadPage(0, isRefresh = true)
        }
    }

    private fun loadCaches() {
        viewModelScope.launch {
            when (val result = accountRepository.getAll()) {
                is Result.Success -> updateAccountCache(result.data)
                is Result.Error -> { /* cached data may be incomplete */ }
            }
        }
        viewModelScope.launch {
            when (val result = languageTypeRepository.getAll()) {
                is Result.Success -> updateLanguageTypeCache(result.data)
                is Result.Error -> { /* cached data may be incomplete */ }
            }
        }
    }

    private fun loadPage(page: Int, isRefresh: Boolean) {
        viewModelScope.launch {
            val offset = page * PAGE_SIZE
            val orderBy = when (_uiState.value.selectedTab) {
                0 -> "created_at"
                1 -> "likes_count"
                else -> "created_at"
            }
            _uiState.update {
                it.copy(
                    isLoading = page == 0 && !isRefresh,
                    isLoadingMore = page > 0,
                    isRefreshing = isRefresh,
                    error = null,
                )
            }
            when (val result = questionRepository.getAll(offset, PAGE_SIZE, orderBy, ascending = false)) {
                is Result.Success -> {
                    val uiModels = result.data.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                    _uiState.update {
                        it.copy(
                            questions = if (page == 0) uiModels else it.questions + uiModels,
                            currentPage = page,
                            hasMore = result.data.size == PAGE_SIZE,
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.error.toUIText(),
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                        )
                    }
                }
            }
        }
    }
}
