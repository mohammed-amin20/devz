package com.mohamed.devz.feature.question.presentation.add_edit_question

import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.presentation.util.UiText

data class AddEditQuestionState(
    val title: String = "",
    val body: String = "",
    val code: String = "",
    val selectedLangTypeId: Int = 1,
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val showTagInput: Boolean = false,
    val languageTypes: List<LanguageType> = emptyList(),
    val isLoading: Boolean = false,
    val createdAt: String? = null,
    val isEdit: Boolean = false,
    val editQuestionId: Int? = null,
    val error: UiText? = null,
)
