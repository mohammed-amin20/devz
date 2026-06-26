package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.SearchHistoryRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.question.presentation.view_questions.util.toFeedUiModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.getCachedAccountIds
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateAccountCache
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateLanguageTypeCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 10

@HiltViewModel
class ViewQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val accountRepository: AccountRepository,
    private val languageTypeRepository: LanguageTypeRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewQuestionsState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadLanguageTypeCache()
        observeSearchQuery()
        setupPersonalizedFeed()
    }

    private fun setupPersonalizedFeed(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update {
                    it.copy(
                        questions = emptyList(),
                        currentPage = 0,
                        hasMore = true,
                        isRefreshing = true,
                        error = null,
                    )
                }
            }

            val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: return@launch
            if (accountId == 0) return@launch

            val skills = when (val accountResult = accountRepository.getById(accountId)) {
                is Result.Success -> accountResult.data.techStack
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                is Result.Error -> null
            }

            val searchHistoryResult = searchHistoryRepository.getRecentByAccountId(accountId, 10)
            val searchTerms = when (searchHistoryResult) {
                is Result.Success -> searchHistoryResult.data.map { it.query.trim() }.filter { it.isNotEmpty() }
                is Result.Error -> null
            }

            val confirmedNoData =
                skills != null && skills.isEmpty() &&
                searchTerms != null && searchTerms.isEmpty()

            if (confirmedNoData) {
                _uiState.update { it.copy(hasPersonalizedFeed = false, isRefreshing = false) }
                return@launch
            }

            val mergedTags = ((skills ?: emptyList()) + (searchTerms ?: emptyList())).distinct()
            _uiState.update { it.copy(hasPersonalizedFeed = true, personalizationTags = mergedTags) }
            loadPage(0, isRefresh = isRefresh)
        }
    }

    fun onAction(action: ViewQuestionsAction) {
        when (action) {
            is ViewQuestionsAction.LoadInitialQuestions -> {
                if (!_uiState.value.hasPersonalizedFeed && _uiState.value.personalizationTags.isEmpty()) {
                    setupPersonalizedFeed()
                } else {
                    loadPage(0, isRefresh = false)
                }
            }
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
            is ViewQuestionsAction.Refresh -> setupPersonalizedFeed(isRefresh = true)
        }
    }

    private fun loadLanguageTypeCache() {
        viewModelScope.launch {
            when (val result = languageTypeRepository.getAll()) {
                is Result.Success -> updateLanguageTypeCache(result.data)
                is Result.Error -> { }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            val accountId = userPreferencesRepository
                .observeCurrentAccountId()
                .first() ?: return@launch
            if (accountId == 0) return@launch

            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) return@collect
                    searchHistoryRepository.insert(query, accountId)
                    _uiState.update { it.copy(currentPage = 0, questions = emptyList(), hasMore = true) }
                    loadPage(0, isRefresh = false)
                }
        }
    }

    private fun loadPage(page: Int, isRefresh: Boolean) {
        viewModelScope.launch {
            val offset = page * PAGE_SIZE
            val query = _uiState.value.searchQuery
            val tags = _uiState.value.personalizationTags
            _uiState.update {
                it.copy(
                    isLoading = page == 0 && !isRefresh,
                    isLoadingMore = page > 0,
                    isRefreshing = isRefresh,
                    error = null,
                )
            }
            val result = when {
                query.isNotBlank() -> questionRepository.search(query, offset, PAGE_SIZE)
                tags.isNotEmpty() -> questionRepository.getByTags(tags, offset, PAGE_SIZE)
                else -> questionRepository.getAll(offset, PAGE_SIZE, "created_at", ascending = false)
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
