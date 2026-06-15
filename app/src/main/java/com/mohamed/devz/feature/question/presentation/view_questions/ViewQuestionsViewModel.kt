package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.question.presentation.view_questions.util.toFeedUiModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.getCachedAccountIds
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateAccountCache
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateLanguageTypeCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
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

    private val _searchQuery = MutableStateFlow("")

    init {
        loadLanguageTypeCache()
        observeSearchQuery()
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
                _searchQuery.value = action.value
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

    private fun loadLanguageTypeCache() {
        viewModelScope.launch {
            when (val result = languageTypeRepository.getAll()) {
                is Result.Success -> updateLanguageTypeCache(result.data)
                is Result.Error -> { /* cached data may be incomplete */ }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .drop(1)
                .collect { query ->
                    _uiState.update { it.copy(currentPage = 0, questions = emptyList(), hasMore = true) }
                    loadPage(0, isRefresh = false)
                }
        }
    }

    private fun loadPage(page: Int, isRefresh: Boolean) {
        viewModelScope.launch {
            val offset = page * PAGE_SIZE
            val query = _uiState.value.searchQuery
            _uiState.update {
                it.copy(
                    isLoading = page == 0 && !isRefresh,
                    isLoadingMore = page > 0,
                    isRefreshing = isRefresh,
                    error = null,
                )
            }
            val result = if (query.isBlank()) {
                questionRepository.getAll(offset, PAGE_SIZE, "created_at", ascending = false)
            } else {
                questionRepository.search(query, offset, PAGE_SIZE)
            }
            when (result) {
                is Result.Success -> {
                    val questions = result.data
                    val neededIds = questions.map { it.accountId }.distinct() - getCachedAccountIds()
                    val fetchedAccounts = if (neededIds.isNotEmpty()) {
                        coroutineScope {
                            neededIds.map { id ->
                                async { accountRepository.getById(id) }
                            }.mapNotNull { deferred ->
                                when (val r = deferred.await()) {
                                    is Result.Success -> r.data
                                    is Result.Error -> null
                                }
                            }
                        }
                    } else emptyList()
                    updateAccountCache(fetchedAccounts)
                    val uiModels = questions.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                    _uiState.update {
                        it.copy(
                            questions = if (page == 0) uiModels else it.questions + uiModels,
                            currentPage = page,
                            hasMore = questions.size == PAGE_SIZE,
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
