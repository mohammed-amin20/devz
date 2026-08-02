package com.mohamed.devz.feature.question.presentation.question_details

import com.mohamed.devz.feature.report.presentation.ReportTarget

sealed interface QuestionDetailsAction {
    data class LoadQuestion(val questionId: Int) : QuestionDetailsAction
    data class AnswerTextChanged(val value: String) : QuestionDetailsAction
    data class AnswerCodeChanged(val value: String?) : QuestionDetailsAction
    data object ShowCodeEditor : QuestionDetailsAction
    data object HideCodeEditor : QuestionDetailsAction
    data class PrefillAnswerCode(val code: String) : QuestionDetailsAction
    data class PostAnswer(val onSuccess: () -> Unit) : QuestionDetailsAction
    data object ToggleLike : QuestionDetailsAction
    data class ToggleAnswerVote(val answerId: Int) : QuestionDetailsAction
    data class AcceptAnswer(val answerId: Int) : QuestionDetailsAction
    data object PinQuestion : QuestionDetailsAction
    data class ShowReport(val target: ReportTarget) : QuestionDetailsAction
    data object DismissReport : QuestionDetailsAction
}
