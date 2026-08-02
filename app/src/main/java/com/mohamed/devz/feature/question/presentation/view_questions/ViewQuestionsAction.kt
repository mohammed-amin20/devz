package com.mohamed.devz.feature.question.presentation.view_questions

import com.mohamed.devz.feature.report.presentation.ReportTarget

sealed interface ViewQuestionsAction {
    data object LoadInitialQuestions : ViewQuestionsAction
    data object LoadNextPage : ViewQuestionsAction
    data class SearchQueryChanged(val value: String) : ViewQuestionsAction
    data class TabSelected(val index: Int) : ViewQuestionsAction
    data class ToggleBookmark(val questionId: Int) : ViewQuestionsAction
    data object Refresh : ViewQuestionsAction
    data class ShowReport(val target: ReportTarget) : ViewQuestionsAction
    data object DismissReport : ViewQuestionsAction
}
