package com.mohamed.devz.feature.question.presentation.view_questions

import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.question.presentation.view_questions.util.QuestionFeedUiModel

data class ViewQuestionsState(
    val questions: List<QuestionFeedUiModel> = emptyList(),
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val bookmarkedIds: Set<Int> = emptySet(),
    val error: UiText? = null,
)
