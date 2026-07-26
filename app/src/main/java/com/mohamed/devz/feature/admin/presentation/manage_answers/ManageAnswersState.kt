package com.mohamed.devz.feature.admin.presentation.manage_answers

import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.presentation.util.UiText

data class ManageAnswersState(
    val allAnswers: List<Answer> = emptyList(),
    val filteredAnswers: List<Answer> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val targetDeleteAnswer: Answer? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val authorNames: Map<Int, String> = emptyMap(),
    val questionTitles: Map<Int, String> = emptyMap(),
)
