package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.question.presentation.view_questions.util.QuestionFeedUiModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.accountCache
import com.mohamed.devz.feature.question.presentation.view_questions.util.toFeedUiModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateAccountCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 10

@HiltViewModel
class ViewQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewQuestionsState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
        loadFeed()
    }

    fun onAction(action: ViewQuestionsAction) {
        when (action) {
            is ViewQuestionsAction.LoadInitialQuestions -> loadFeed()
            is ViewQuestionsAction.LoadNextPage -> {
                val state = _uiState.value
                if (state.isLoadingMore || !state.hasMore) return
                if (state.searchQuery.isNotBlank()) {
                    loadSearchPage(state.currentPage + 1)
                } else {
                    loadFeedPage(page = state.currentPage + 1)
                }
            }
            is ViewQuestionsAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
                _searchQuery.value = action.value
            }
            is ViewQuestionsAction.TabSelected -> {
                _uiState.update {
                    it.copy(selectedTab = action.index, currentPage = 0, questions = emptyList(), hasMore = true)
                }
                loadFeed()
            }
            is ViewQuestionsAction.ToggleBookmark -> {
                _uiState.update {
                    val updated = it.bookmarkedIds.toMutableSet()
                    if (action.questionId in updated) updated.remove(action.questionId)
                    else updated.add(action.questionId)
                    it.copy(bookmarkedIds = updated)
                }
            }
            is ViewQuestionsAction.Refresh -> loadFeed(isRefresh = true)
        }
    }

    // ─── Following-based Feed ─────────────────────────────────────────

    private fun loadFeed(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    questions = emptyList(),
                    currentPage = 0,
                    hasMore = true,
                    isLoading = !isRefresh,
                    isLoadingMore = false,
                    isRefreshing = isRefresh,
                    error = null,
                    isNotFollowingAnyone = false,
                )
            }

            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                return@launch
            }

            when (val accountResult = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val followingIds = accountResult.data.followingIds
                        .split(",")
                        .mapNotNull { it.trim().toIntOrNull() }
                        .filter { it > 0 }

                    if (followingIds.isEmpty()) {
                        _uiState.update {
                            it.copy(isLoading = false, isRefreshing = false, isNotFollowingAnyone = true)
                        }
                        return@launch
                    }

                    loadFeedPage(followingIds, page = 0)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, error = accountResult.error.toUIText())
                    }
                }
            }
        }
    }

    private var cachedFollowingIds: List<Int> = emptyList()

    private fun loadFeedPage(followingIds: List<Int>? = null, page: Int) {
        viewModelScope.launch {
            val ids = followingIds ?: cachedFollowingIds
            if (ids.isEmpty()) {
                _uiState.update { it.copy(isLoadingMore = false, isLoading = false, isRefreshing = false) }
                return@launch
            }
            if (followingIds != null) cachedFollowingIds = followingIds

            val offset = page * PAGE_SIZE
            _uiState.update {
                it.copy(
                    isLoading = page == 0 && !it.isRefreshing,
                    isLoadingMore = page > 0,
                    error = null,
                )
            }

            when (val result = questionRepository.getByAccountIds(ids, offset, PAGE_SIZE)) {
                is Result.Success -> {
                    val questions = result.data
                    val needAuthorIds = questions.map { it.accountId }.distinct()
                    val cachedIds = accountCache.keys
                    val missingIds = needAuthorIds - cachedIds
                    if (missingIds.isNotEmpty()) {
                        missingIds.forEach { id ->
                            when (val author = accountRepository.getById(id)) {
                                is Result.Success -> updateAccountCache(listOf(author.data))
                                is Result.Error -> {}
                            }
                        }
                    }
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

    // ─── Search ──────────────────────────────────────────────────────

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        loadFeed()
                        return@collect
                    }
                    _uiState.update { it.copy(currentPage = 0, questions = emptyList(), hasMore = true) }
                    loadSearchPage(0)
                }
        }
    }

    private fun loadSearchPage(page: Int) {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery
            if (query.isBlank()) return@launch
            val offset = page * PAGE_SIZE
            _uiState.update {
                it.copy(
                    isLoading = page == 0,
                    isLoadingMore = page > 0,
                    error = null,
                )
            }
            when (val result = questionRepository.search(query, offset, PAGE_SIZE)) {
                is Result.Success -> {
                    val questions = result.data
                    val needAuthorIds = questions.map { it.accountId }.distinct()
                    val cachedIds = accountCache.keys
                    val missingIds = needAuthorIds - cachedIds
                    if (missingIds.isNotEmpty()) {
                        missingIds.forEach { id ->
                            when (val author = accountRepository.getById(id)) {
                                is Result.Success -> updateAccountCache(listOf(author.data))
                                is Result.Error -> {}
                            }
                        }
                    }
                    val uiModels = questions.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                    _uiState.update {
                        it.copy(
                            questions = if (page == 0) uiModels else it.questions + uiModels,
                            currentPage = page,
                            hasMore = questions.size == PAGE_SIZE,
                            isLoading = false,
                            isLoadingMore = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.error.toUIText(),
                            isLoading = false,
                            isLoadingMore = false,
                        )
                    }
                }
            }
        }
    }
}
