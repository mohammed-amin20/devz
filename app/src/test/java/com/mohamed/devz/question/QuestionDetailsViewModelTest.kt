package com.mohamed.devz.question

import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailsAction
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailsViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var questionRepository: QuestionRepository
    private lateinit var answerRepository: AnswerRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var fcmPushSender: FcmPushSender

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = mockk()
        answerRepository = mockk()
        accountRepository = mockk()
        userPreferencesRepository = mockk()
        notificationRepository = mockk()
        fcmPushSender = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load question updates state with question details`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { questionRepository.getById(100) } returns Result.Success(
            Question(100, "Test Title", "Test Body", "code", 5, 3, "kotlin", 1, 42,
                "2024-01-15T10:00:00Z", "10,20")
        )
        coEvery { accountRepository.getById(42) } returns Result.Success(
            Account(42, "author", "Author Name", "", "", "https://example.com/avatar.jpg",
                "", "", "", "", "")
        )
        coEvery { answerRepository.getByQuestionId(100) } returns Result.Success(emptyList())

        val viewModel = QuestionDetailsViewModel(
            questionRepository, answerRepository, accountRepository,
            userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        viewModel.onAction(QuestionDetailsAction.LoadQuestion(100))
        advanceUntilIdle()

        val question = viewModel.uiState.value.question
        assertNotNull(question)
        assertEquals("Test Title", question!!.title)
        assertEquals("Test Body", question.body)
        assertEquals("code", question.code)
        assertEquals("Author Name", question.authorName)
        assertEquals(5, question.likes)
        assertEquals(3, question.answersCount)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `load question with error shows error`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { questionRepository.getById(999) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.NotFound)
        val viewModel = QuestionDetailsViewModel(
            questionRepository, answerRepository, accountRepository,
            userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()
        viewModel.onAction(QuestionDetailsAction.LoadQuestion(999))
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.question)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `answer text changed updates state`() {
        val viewModel = createViewModel()
        viewModel.onAction(QuestionDetailsAction.AnswerTextChanged("My answer"))
        assertEquals("My answer", viewModel.uiState.value.answerText)
    }

    @Test
    fun `post answer inserts and updates state`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { questionRepository.getById(100) } returns Result.Success(
            Question(100, "Title", "Body", "", 0, 0, "", 1, 42, "2024-01-15T10:00:00Z", "")
        )
        coEvery { accountRepository.getById(42) } returns Result.Success(
            Account(42, "a", "Author", "", "", "", "", "", "", "", websiteUrl = "")
        )
        coEvery { answerRepository.getByQuestionId(100) } returns Result.Success(emptyList())
        coEvery { answerRepository.insert(any()) } returns Result.Success(
            Answer(1, "My answer", false, "", 100, 1, "2024-01-16T10:00:00Z")
        )
        coEvery { questionRepository.incrementAnswerCount(100, 0) } returns Result.Success(Unit)
        coEvery { accountRepository.addPoints(any(), any()) } returns Result.Success(Unit)
        coEvery { notificationRepository.insert(any()) } returns Result.Success(mockk())

        var onSuccessCalled = false
        val viewModel = QuestionDetailsViewModel(
            questionRepository, answerRepository, accountRepository,
            userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        viewModel.onAction(QuestionDetailsAction.LoadQuestion(100))
        advanceUntilIdle()

        viewModel.onAction(QuestionDetailsAction.AnswerTextChanged("My answer"))
        viewModel.onAction(QuestionDetailsAction.PostAnswer { onSuccessCalled = true })
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
    }

    @Test
    fun `toggle like optimistically updates and reverts on error`() = runTest(testDispatcher) {
        val currentAccount = Account(1, "me", "Me", "", "", "", "", "", "", "", websiteUrl = "")
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { questionRepository.getById(100) } returns Result.Success(
            Question(100, "Title", "Body", "", 5, 0, "", 1, 2, "2024-01-15T10:00:00Z", "10,20")
        )
        coEvery { accountRepository.getById(2) } returns Result.Success(
            Account(2, "a", "Author", "", "", "", "", "", "", "", "")
)
        coEvery { answerRepository.getByQuestionId(100) } returns Result.Success(emptyList())

        // repositroy returns error to test optimistic rollback
        coEvery { questionRepository.toggleLike(100, "10,20,1", 3) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)

        val viewModel = QuestionDetailsViewModel(
            questionRepository, answerRepository, accountRepository,
            userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        viewModel.onAction(QuestionDetailsAction.LoadQuestion(100))
        advanceUntilIdle()

        val originalLikes = viewModel.uiState.value.question!!.likes
        viewModel.onAction(QuestionDetailsAction.ToggleLike)
        advanceUntilIdle()

        // Optimistic update should be rolled back
        assertEquals(originalLikes, viewModel.uiState.value.question!!.likes)
    }

    private fun createViewModel(): QuestionDetailsViewModel {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        return QuestionDetailsViewModel(
            questionRepository, answerRepository, accountRepository,
            userPreferencesRepository, notificationRepository, fcmPushSender
        )
    }
}
