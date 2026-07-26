package com.mohamed.devz.feature.admin.presentation.manage_answers

import com.mohamed.devz.feature.core.domain.model.Answer

sealed interface ManageAnswersAction {
    data class SearchQueryChanged(val value: String) : ManageAnswersAction
    data class DeleteAnswer(val answer: Answer) : ManageAnswersAction
    data class ConfirmDelete(val answer: Answer) : ManageAnswersAction
    data object DismissDeleteDialog : ManageAnswersAction
    data object Refresh : ManageAnswersAction
}
