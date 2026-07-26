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

    private var cachedFollowingIds: List<Int> = emptyList()
    private var cachedTechStackTags: List<String> = emptyList()

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
                } else if (state.selectedTab == 0) {
                    loadTechStackPage(state.currentPage + 1)
                } else {
                    loadFollowingPage(state.currentPage + 1)
                }
            }
            is ViewQuestionsAction.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = action.value) }
                _searchQuery.value = action.value
            }
            is ViewQuestionsAction.TabSelected -> {
                _uiState.update {
                    it.copy(
                        selectedTab = action.index,
                        currentPage = 0,
                        questions = emptyList(),
                        hasMore = true,
                        noTechMatches = false,
                        isNotFollowingAnyone = false,
                    )
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

    // ─── Feed Router ──────────────────────────────────────────────────

    private fun loadFeed(isRefresh: Boolean = false) {
        if (_uiState.value.selectedTab == 0) {
            loadTechStackFeed(isRefresh)
        } else {
            loadFollowingFeed(isRefresh)
        }
    }

    // ─── Tech-Stack Feed (Tab 0) ─────────────────────────────────────

    private fun loadTechStackFeed(isRefresh: Boolean = false) {
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
                    noTechMatches = false,
                )
            }

            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            if (accountId == 0) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                return@launch
            }

            when (val accountResult = accountRepository.getById(accountId)) {
                is Result.Success -> {
                    val techStack = accountResult.data.techStack
                    val tags = techStack.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    cachedTechStackTags = tags

                    if (tags.isEmpty()) {
                        _uiState.update {
                            it.copy(isLoading = false, isRefreshing = false, noTechMatches = true)
                        }
                        return@launch
                    }

                    loadTechStackPage(0)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, error = accountResult.error.toUIText())
                    }
                }
            }
        }
    }

    private fun loadTechStackPage(page: Int) {
        viewModelScope.launch {
            val tags = cachedTechStackTags
            if (tags.isEmpty()) {
                _uiState.update { it.copy(isLoadingMore = false, isLoading = false, isRefreshing = false, noTechMatches = true) }
                return@launch
            }

            val offset = page * PAGE_SIZE
            _uiState.update {
                it.copy(
                    isLoading = page == 0 && !it.isRefreshing,
                    isLoadingMore = page > 0,
                    error = null,
                )
            }

            when (val result = questionRepository.getByTags(tags, offset, PAGE_SIZE)) {
                is Result.Success -> {
                    val questions = result.data
                    cacheAuthors(questions.map { it.accountId })
                    val uiModels = questions.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                    _uiState.update {
                        it.copy(
                            questions = if (page == 0) uiModels else it.questions + uiModels,
                            currentPage = page,
                            hasMore = questions.size == PAGE_SIZE,
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            noTechMatches = page == 0 && uiModels.isEmpty(),
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

    // ─── Following Feed (Tab 1) ──────────────────────────────────────

    private fun loadFollowingFeed(isRefresh: Boolean = false) {
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

                    loadFollowingPage(0, followingIds)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, error = accountResult.error.toUIText())
                    }
                }
            }
        }
    }

    private fun loadFollowingPage(page: Int, initialIds: List<Int>? = null) {
        viewModelScope.launch {
            val ids = initialIds ?: cachedFollowingIds
            if (ids.isEmpty()) {
                _uiState.update { it.copy(isLoadingMore = false, isLoading = false, isRefreshing = false) }
                return@launch
            }
            if (initialIds != null) cachedFollowingIds = initialIds

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
                    cacheAuthors(questions.map { it.accountId })
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

            val questionResult = questionRepository.search(query, offset, PAGE_SIZE)
            val accountResult = accountRepository.searchAccounts(query)
            val accountIds = (accountResult as? Result.Success)?.data?.map { it.id } ?: emptyList()

            val accountQuestions = if (accountIds.isNotEmpty()) {
                val result = questionRepository.getByAccountIds(accountIds, 0, PAGE_SIZE)
                (result as? Result.Success)?.data ?: emptyList()
            } else emptyList()

            when (val qResult = questionResult) {
                is Result.Success -> {
                    val allQuestions = (qResult.data + accountQuestions)
                        .distinctBy { it.id }
                        .sortedByDescending { it.createdAt }

                    cacheAuthors(allQuestions.map { it.accountId })

                    val uiModels = allQuestions.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                    _uiState.update {
                        it.copy(
                            questions = if (page == 0) uiModels else it.questions + uiModels,
                            currentPage = page,
                            hasMore = allQuestions.size == PAGE_SIZE,
                            isLoading = false,
                            isLoadingMore = false,
                        )
                    }
                }
                is Result.Error -> {
                    if (accountQuestions.isNotEmpty()) {
                        cacheAuthors(accountQuestions.map { it.accountId })
                        val uiModels = accountQuestions.map { it.toFeedUiModel(_uiState.value.bookmarkedIds) }
                        _uiState.update {
                            it.copy(
                                questions = if (page == 0) uiModels else it.questions + uiModels,
                                currentPage = page,
                                hasMore = false,
                                isLoading = false,
                                isLoadingMore = false,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                error = qResult.error.toUIText(),
                                isLoading = false,
                                isLoadingMore = false,
                            )
                        }
                    }
                }
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────

    private suspend fun cacheAuthors(accountIds: List<Int>) {
        val missingIds = accountIds.distinct() - accountCache.keys
        missingIds.forEach { id ->
            when (val author = accountRepository.getById(id)) {
                is Result.Success -> updateAccountCache(listOf(author.data))
                is Result.Error -> {}
            }
        }
    }
}
