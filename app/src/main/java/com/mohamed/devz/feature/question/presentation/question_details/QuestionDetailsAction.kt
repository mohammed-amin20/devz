package com.mohamed.devz.feature.question.presentation.question_details

sealed interface QuestionDetailsAction {
    data class LoadQuestion(val questionId: Int) : QuestionDetailsAction
    data class AnswerTextChanged(val value: String) : QuestionDetailsAction
    data class PostAnswer(val onSuccess: () -> Unit) : QuestionDetailsAction
    data object ToggleLike : QuestionDetailsAction
    data class ToggleAnswerVote(val answerId: Int) : QuestionDetailsAction
    data class AcceptAnswer(val answerId: Int) : QuestionDetailsAction
}
