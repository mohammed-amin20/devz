package com.mohamed.devz.profile

import androidx.lifecycle.SavedStateHandle
import com.mohamed.devz.feature.core.data.data_source.local.FcmPushSender
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileAction
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileEvent
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var accountRepository: AccountRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var answerRepository: AnswerRepository
    private lateinit var jobRepository: JobRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var fcmPushSender: FcmPushSender

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = mockk()
        accountRepository = mockk()
        questionRepository = mockk()
        answerRepository = mockk()
        jobRepository = mockk()
        userPreferencesRepository = mockk()
        notificationRepository = mockk()
        fcmPushSender = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load profile populates state for own profile`() = runTest(testDispatcher) {
        val account = Account(id = 1, username = "johndoe", fullName = "John Doe",
            email = "john@test.com", password = "pwd", imageUrl = "https://example.com/avatar.jpg",
            bio = "Android dev", techStack = "Kotlin,Compose", githubUrl = "https://github.com/johndoe",
            linkedInUrl = "", websiteUrl = "", points = 50, fcmToken = "", followerIds = "2,3",
            followingIds = "4,5")
        every { savedStateHandle.get<Int>("accountId") } returns null
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)
        coEvery { questionRepository.getByAccountId(1) } returns Result.Success(emptyList())
        coEvery { answerRepository.getByAccountId(1) } returns Result.Success(emptyList())
        val viewModel = ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.isOwnProfile)
        assertEquals("John Doe", state.profile?.fullName)
        assertEquals("johndoe", state.profile?.username)
        assertEquals("50", state.profile?.points)
        assertEquals("Android dev", state.profile?.bio)
        assertEquals(listOf("Kotlin", "Compose"), state.profile?.skills)
        assertEquals(2, state.followersCount)
        assertEquals(2, state.followingCount)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `load profile with error shows error`() = runTest(testDispatcher) {
        every { savedStateHandle.get<Int>("accountId") } returns null
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)
        val viewModel = ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `accountId zero shows user not found error`() = runTest(testDispatcher) {
        every { savedStateHandle.get<Int>("accountId") } returns null
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(0)

        val viewModel = ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `logout clears preferences and emits NavigateToAuth`() = runTest(testDispatcher) {
        val account = Account(1, "me", "Me", "", "", "", "", "", "", "", websiteUrl = "")
        every { savedStateHandle.get<Int>("accountId") } returns null
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)
        coEvery { questionRepository.getByAccountId(1) } returns Result.Success(emptyList())
        coEvery { answerRepository.getByAccountId(1) } returns Result.Success(emptyList())
        coEvery { userPreferencesRepository.setLoggedOut() } returns Result.Success(Unit)
        coEvery { userPreferencesRepository.clearAccountId() } returns Result.Success(Unit)

        val viewModel = ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        val events = mutableListOf<ProfileEvent>()
        val job = launch { viewModel.profileEvent.collect { events.add(it) } }

        viewModel.onAction(ProfileAction.Logout)
        advanceUntilIdle()
        job.cancel()

        assertEquals(1, events.size)
        assertEquals(ProfileEvent.NavigateToAuth, events[0])
    }

    @Test
    fun `toggle follow optimistically updates and reverts on error`() = runTest(testDispatcher) {
        val currentAccount = Account(1, "me", "Me", "", "", "", "", "", "", "", "", followingIds = "")
        val targetAccount = Account(2, "other", "Other", "", "", "", "", "", "", "", "",
            followerIds = "")

        every { savedStateHandle.get<Int>("accountId") } returns 2
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(2) } returns Result.Success(targetAccount)
        coEvery { accountRepository.getById(1) } returns Result.Success(currentAccount)
        coEvery { questionRepository.getByAccountId(2) } returns Result.Success(emptyList())
        coEvery { answerRepository.getByAccountId(2) } returns Result.Success(emptyList())
        coEvery { accountRepository.follow(1, 2) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)

        val viewModel = ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
        advanceUntilIdle()

        val initialFollowing = viewModel.uiState.value.isFollowing
        viewModel.onAction(ProfileAction.ToggleFollow(2))
        advanceUntilIdle()

        assertEquals(initialFollowing, viewModel.uiState.value.isFollowing)
    }

    @Test
    fun `dismiss dialog resets dialog state`() {
        val viewModel = createViewModel()
        viewModel.onAction(ProfileAction.DismissDialog)

        assertEquals(false, viewModel.uiState.value.showFollowersDialog)
        assertEquals(false, viewModel.uiState.value.showFollowingDialog)
        assertTrue(viewModel.uiState.value.followerAccounts.isEmpty())
        assertTrue(viewModel.uiState.value.followingAccounts.isEmpty())
    }

    private fun createViewModel(): ProfileViewModel {
        val account = Account(1, "me", "Me", "", "", "", "", "", "", "", websiteUrl = "")
        every { savedStateHandle.get<Int>("accountId") } returns null
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)
        coEvery { questionRepository.getByAccountId(1) } returns Result.Success(emptyList())
        coEvery { answerRepository.getByAccountId(1) } returns Result.Success(emptyList())
        return ProfileViewModel(
            savedStateHandle, accountRepository, questionRepository, answerRepository,
            jobRepository, userPreferencesRepository, notificationRepository, fcmPushSender
        )
    }
}
