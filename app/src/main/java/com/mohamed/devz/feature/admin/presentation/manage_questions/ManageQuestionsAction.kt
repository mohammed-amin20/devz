package com.mohamed.devz.feature.admin.presentation.manage_questions

import com.mohamed.devz.feature.core.domain.model.Question

sealed interface ManageQuestionsAction {
    data class SearchQueryChanged(val value: String) : ManageQuestionsAction
    data object FilterAll : ManageQuestionsAction
    data object FilterHidden : ManageQuestionsAction
    data class HideQuestion(val question: Question) : ManageQuestionsAction
    data class ConfirmHide(val question: Question) : ManageQuestionsAction
    data object DismissHideDialog : ManageQuestionsAction
    data class UnhideQuestion(val question: Question) : ManageQuestionsAction
    data object Refresh : ManageQuestionsAction
}
