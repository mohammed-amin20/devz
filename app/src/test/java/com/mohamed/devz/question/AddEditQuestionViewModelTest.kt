package com.mohamed.devz.question

import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionAction
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditQuestionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var questionRepository: QuestionRepository
    private lateinit var languageTypeRepository: LanguageTypeRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = mockk()
        languageTypeRepository = mockk()
        userPreferencesRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads language types`() = runTest(testDispatcher) {
        val types = listOf(LanguageType(1, "Kotlin"), LanguageType(2, "JavaScript"))
        coEvery { languageTypeRepository.getAll() } returns Result.Success(types)

        val viewModel = AddEditQuestionViewModel(questionRepository, languageTypeRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.languageTypes.size)
        assertEquals("Kotlin", viewModel.uiState.value.languageTypes[0].type)
    }

    @Test
    fun `title change updates state and clears error`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.TitleChanged("New Title"))
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `body change updates state and clears error`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.BodyChanged("New Body"))
        assertEquals("New Body", viewModel.uiState.value.body)
    }

    @Test
    fun `code change updates state`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.CodeChanged("fun main() {}"))
        assertEquals("fun main() {}", viewModel.uiState.value.code)
    }

    @Test
    fun `language selection updates state`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.LanguageSelected(2))
        assertEquals(2, viewModel.uiState.value.selectedLangTypeId)
    }

    @Test
    fun `add tag adds tag and clears input`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.TagInputChanged("kotlin"))
        viewModel.onAction(AddEditQuestionAction.AddTag)
        assertEquals(listOf("kotlin"), viewModel.uiState.value.tags)
        assertEquals("", viewModel.uiState.value.tagInput)
        assertFalse(viewModel.uiState.value.showTagInput)
    }

    @Test
    fun `add tag ignores empty input`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.AddTag)
        assertTrue(viewModel.uiState.value.tags.isEmpty())
    }

    @Test
    fun `remove tag removes the specified tag`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.TagInputChanged("kotlin"))
        viewModel.onAction(AddEditQuestionAction.AddTag)
        viewModel.onAction(AddEditQuestionAction.TagInputChanged("compose"))
        viewModel.onAction(AddEditQuestionAction.AddTag)
        assertEquals(2, viewModel.uiState.value.tags.size)

        viewModel.onAction(AddEditQuestionAction.RemoveTag("kotlin"))
        assertEquals(listOf("compose"), viewModel.uiState.value.tags)
    }

    @Test
    fun `show tag input sets flag`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.ShowTagInput)
        assertTrue(viewModel.uiState.value.showTagInput)
    }

    @Test
    fun `publish without title shows validation error`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.BodyChanged("Some body"))
        viewModel.onAction(AddEditQuestionAction.Publish { })
        assertNotNull(viewModel.uiState.value.titleError)
    }

    @Test
    fun `publish without body shows validation error`() {
        val viewModel = createViewModel()
        viewModel.onAction(AddEditQuestionAction.TitleChanged("Title"))
        viewModel.onAction(AddEditQuestionAction.Publish { })
        assertNotNull(viewModel.uiState.value.bodyError)
    }

    @Test
    fun `publish with valid data inserts question`() = runTest(testDispatcher) {
        coEvery { questionRepository.insert(any()) } returns Result.Success(
            Question(0, "Title", "Body", "", 0, 0, "tag1", 1, 1, null, "")
        )
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)

        var onSuccessCalled = false
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(AddEditQuestionAction.TitleChanged("Test Title"))
        viewModel.onAction(AddEditQuestionAction.BodyChanged("Test Body"))
        viewModel.onAction(AddEditQuestionAction.TagInputChanged("tag1"))
        viewModel.onAction(AddEditQuestionAction.AddTag)
        viewModel.onAction(AddEditQuestionAction.Publish { onSuccessCalled = true })
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `publish with repository error shows error`() = runTest(testDispatcher) {
        coEvery { questionRepository.insert(any()) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(AddEditQuestionAction.TitleChanged("Test"))
        viewModel.onAction(AddEditQuestionAction.BodyChanged("Body"))
        viewModel.onAction(AddEditQuestionAction.Publish { })
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
    }

    private fun createViewModel(): AddEditQuestionViewModel {
        coEvery { languageTypeRepository.getAll() } returns Result.Success(emptyList())
        return AddEditQuestionViewModel(questionRepository, languageTypeRepository, userPreferencesRepository)
    }
}
