package com.mohamed.devz.feature.question.presentation.question_details

import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerUiModel
import com.mohamed.devz.feature.core.presentation.util.UiText

data class QuestionDetailsState(
    val question: QuestionDetailUiModel? = null,
    val answers: List<AnswerUiModel> = emptyList(),
    val answerText: String = "",
    val currentAccountId: Int = 0,
    val isLoading: Boolean = false,
    val isPosting: Boolean = false,
    val isLiking: Boolean = false,
    val error: UiText? = null,
)
