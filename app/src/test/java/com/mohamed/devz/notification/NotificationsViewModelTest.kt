package com.mohamed.devz.notification

import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.notification.presentation.NotificationsAction
import com.mohamed.devz.feature.notification.presentation.NotificationsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        notificationRepository = mockk()
        questionRepository = mockk()
        userPreferencesRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads notifications`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { notificationRepository.getAllByAccountId(1) } returns Result.Success(
            listOf(
                DomainNotification(id = 1, typeId = 2, userId = 1, actorId = 5, questionId = 100,
                    answerId = null, type = "like", message = "liked your question",
                    isRead = false, createdAt = "2024-01-15T10:00:00Z", actorName = "John",
                    actorAvatarUrl = null),
                DomainNotification(id = 2, typeId = 3, userId = 1, actorId = 6, questionId = 200,
                    answerId = 50, type = "answer", message = "answered your question",
                    isRead = true, createdAt = "2024-01-14T10:00:00Z", actorName = "Jane",
                    actorAvatarUrl = null),
            )
        )
        coEvery { questionRepository.getById(100) } returns Result.Success(
            com.mohamed.devz.feature.core.domain.model.Question(100, "Question 1", "", "", 0, 0, "", 1, 1, null, "")
        )
        coEvery { questionRepository.getById(200) } returns Result.Success(
            com.mohamed.devz.feature.core.domain.model.Question(200, "Question 2", "", "", 0, 0, "", 1, 1, null, "")
        )
        val viewModel = NotificationsViewModel(notificationRepository, questionRepository, userPreferencesRepository)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.notifications.size)
        assertEquals("LIKE", viewModel.uiState.value.notifications[0].type.name)
        assertEquals("ANSWER", viewModel.uiState.value.notifications[1].type.name)
        assertEquals(false, viewModel.uiState.value.notifications[0].isRead)
        assertEquals(true, viewModel.uiState.value.notifications[1].isRead)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with accountId zero shows error`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(0)

        val viewModel = NotificationsViewModel(notificationRepository,
            questionRepository, userPreferencesRepository)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with repository error shows error`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { notificationRepository.getAllByAccountId(1) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)

        val viewModel = NotificationsViewModel(notificationRepository, questionRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `mark all read updates all notifications`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { notificationRepository.getAllByAccountId(1) } returns Result.Success(
            listOf(
                DomainNotification(id = 1, typeId = 2, userId = 1, actorId = 5, questionId = 100,
                    answerId = null, type = "like", message = "liked", isRead = false,
                    createdAt = "2024-01-15T10:00:00Z", actorName = "John"),
                DomainNotification(id = 2, typeId = 3, userId = 1, actorId = 6, questionId = 200,
                    answerId = null, type = "answer", message = "answered", isRead = false,
                    createdAt = "2024-01-14T10:00:00Z", actorName = "Jane"),
            )
        )
        coEvery { questionRepository.getById(any()) } returns Result.Success(
            com.mohamed.devz.feature.core.domain.model.Question(0, "", "", "", 0, 0, "", 1, 1, null, "")
        )
        coEvery { notificationRepository.update(any()) } returns Result.Success(Unit)

        val viewModel = NotificationsViewModel(notificationRepository, questionRepository, userPreferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(NotificationsAction.MarkAllRead)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.notifications.all { it.isRead })
    }

    @Test
    fun `mark read updates single notification`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { notificationRepository.getAllByAccountId(1) } returns Result.Success(
            listOf(
                DomainNotification(id = 1, typeId = 2, userId = 1, actorId = 5, questionId = 100,
                    answerId = null, type = "like", message = "liked", isRead = false,
                    createdAt = "2024-01-15T10:00:00Z", actorName = "John"),
            )
        )
        coEvery { questionRepository.getById(100) } returns Result.Success(
            com.mohamed.devz.feature.core.domain.model.Question(100, "Q", "", "", 0, 0, "", 1, 1, null, "")
        )
        coEvery { notificationRepository.update(any()) } returns Result.Success(Unit)

        val viewModel = NotificationsViewModel(notificationRepository, questionRepository, userPreferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(NotificationsAction.MarkRead("1"))
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.notifications[0].isRead)
    }

    @Test
    fun `refresh reloads notifications`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { notificationRepository.getAllByAccountId(1) } returns Result.Success(emptyList())

        val viewModel = NotificationsViewModel(notificationRepository, questionRepository, userPreferencesRepository)
        advanceUntilIdle()

        coEvery { notificationRepository.getAllByAccountId(1) } returns Result.Success(
            listOf(DomainNotification(1, 2, 1, 5, 100, null, "like", "liked", false, "", "John"))
        )
        coEvery { questionRepository.getById(100) } returns Result.Success(
            com.mohamed.devz.feature.core.domain.model.Question(100, "Q", "", "", 0, 0, "", 1, 1, null, "")
        )

        viewModel.onAction(NotificationsAction.Refresh)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.notifications.size)
    }
}
