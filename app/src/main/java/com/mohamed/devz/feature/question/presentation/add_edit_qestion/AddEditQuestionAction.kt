package com.mohamed.devz.feature.question.presentation.add_edit_qestion

sealed interface AddEditQuestionAction {
    data class LoadQuestion(val questionId: Int) : AddEditQuestionAction
    data class TitleChanged(val value: String) : AddEditQuestionAction
    data class BodyChanged(val value: String) : AddEditQuestionAction
    data class CodeChanged(val value: String) : AddEditQuestionAction
    data class LanguageSelected(val langTypeId: Int) : AddEditQuestionAction
    data class TagInputChanged(val value: String) : AddEditQuestionAction
    data object AddTag : AddEditQuestionAction
    data class RemoveTag(val tag: String) : AddEditQuestionAction
    data object ShowTagInput : AddEditQuestionAction
    data class Publish(val onSuccess: () -> Unit) : AddEditQuestionAction
}
