package com.mohamed.devz.feature.admin.presentation.manage_questions

import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ManageQuestionsState(
    val allQuestions: List<Question> = emptyList(),
    val filteredQuestions: List<Question> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: Int = 0,
    val showHideDialog: Boolean = false,
    val targetHideQuestion: Question? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val authorNames: Map<Int, String> = emptyMap(),
)
