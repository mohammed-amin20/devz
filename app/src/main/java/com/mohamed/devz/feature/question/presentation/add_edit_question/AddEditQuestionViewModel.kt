package com.mohamed.devz.feature.question.presentation.add_edit_question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.domain.util.toUIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditQuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val languageTypeRepository: LanguageTypeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditQuestionState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLanguageTypes()
    }

    fun onAction(action: AddEditQuestionAction) {
        when (action) {
            is AddEditQuestionAction.LoadQuestion -> loadQuestion(action.questionId)
            is AddEditQuestionAction.TitleChanged -> _uiState.update { it.copy(title = action.value) }
            is AddEditQuestionAction.BodyChanged -> _uiState.update { it.copy(body = action.value) }
            is AddEditQuestionAction.CodeChanged -> _uiState.update { it.copy(code = action.value) }
            is AddEditQuestionAction.LanguageSelected -> _uiState.update { it.copy(selectedLangTypeId = action.langTypeId) }
            is AddEditQuestionAction.TagInputChanged -> _uiState.update { it.copy(tagInput = action.value) }
            is AddEditQuestionAction.AddTag -> {
                val tag = _uiState.value.tagInput.trim()
                if (tag.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            tags = it.tags + tag,
                            tagInput = "",
                            showTagInput = false,
                        )
                    }
                }
            }
            is AddEditQuestionAction.RemoveTag -> _uiState.update {
                it.copy(tags = it.tags - action.tag)
            }
            is AddEditQuestionAction.ShowTagInput -> _uiState.update { it.copy(showTagInput = true) }
            is AddEditQuestionAction.Publish -> publish(action.onSuccess)
        }
    }

    private fun loadLanguageTypes() {
        viewModelScope.launch {
            when (val result = languageTypeRepository.getAll()) {
                is Result.Success -> _uiState.update { it.copy(languageTypes = result.data) }
                is Result.Error -> { }
            }
        }
    }

    private fun loadQuestion(questionId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEdit = true, editQuestionId = questionId) }
            when (val result = questionRepository.getById(questionId)) {
                is Result.Success -> {
                    val q = result.data
                    _uiState.update {
                        it.copy(
                            title = q.title,
                            body = q.description,
                            code = q.code,
                            selectedLangTypeId = q.langTypeId,
                            tags = q.tags.split(",").filter { tag -> tag.isNotBlank() },
                            createdAt = q.createdAt,
                            isLoading = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.error.toUIText(), isLoading = false) }
                }
            }
        }
    }

    private fun publish(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val currentAccountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
            val question = Question(
                id = state.editQuestionId ?: 0,
                title = state.title,
                description = state.body,
                code = state.code,
                likesCount = 0,
                answersCount = 0,
                tags = state.tags.joinToString(","),
                langTypeId = state.selectedLangTypeId,
                accountId = currentAccountId,
                createdAt = state.createdAt,
            )
            if (state.isEdit) {
                when (val r = questionRepository.update(question)) {
                    is Result.Error -> {
                        _uiState.update { it.copy(error = r.error.toUIText(), isLoading = false) }
                        return@launch
                    }
                    is Result.Success -> {}
                }
            } else {
                when (val r = questionRepository.insert(question)) {
                    is Result.Error -> {
                        _uiState.update { it.copy(error = r.error.toUIText(), isLoading = false) }
                        return@launch
                    }
                    is Result.Success -> {}
                }
            }
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
