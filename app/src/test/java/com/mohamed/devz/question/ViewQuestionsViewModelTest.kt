package com.mohamed.devz.question

import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsAction
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsViewModel
import com.mohamed.devz.feature.question.presentation.view_questions.util.accountCache
import com.mohamed.devz.feature.question.presentation.view_questions.util.updateLanguageTypeCache
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
class ViewQuestionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var questionRepository: QuestionRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = mockk()
        accountRepository = mockk()
        userPreferencesRepository = mockk()
        accountCache.clear()
        updateLanguageTypeCache(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads feed when user follows others`() = runTest(testDispatcher) {
        val currentAccount = Account(id = 1, username = "me", fullName = "Me",
            email = "", password = "", imageUrl = "", bio = "", techStack = "",
            githubUrl = "", linkedInUrl = "", websiteUrl = "", points = 0, fcmToken = "",
            followerIds = "", followingIds = "2,3")
        val author = Account(id = 2, username = "author", fullName = "Author Name",
            email = "", password = "", imageUrl = "", bio = "", techStack = "",
            githubUrl = "", linkedInUrl = "", websiteUrl = "", points = 0, fcmToken = "",
            followerIds = "", followingIds = "")

        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(currentAccount)
        coEvery { accountRepository.getById(2) } returns Result.Success(author)
        coEvery { questionRepository.getByAccountIds(listOf(2, 3), 0, 10) } returns Result.Success(
            listOf(Question(1, "Title", "Body", "", 5, 2, "kotlin", 1, 2, "2024-01-15T10:00:00Z", ""))
        )

        val viewModel = ViewQuestionsViewModel(questionRepository, accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.questions.size)
        assertEquals("Title", viewModel.uiState.value.questions[0].title)
    }

    @Test
    fun `init shows notFollowingAnyone when user follows no one`() = runTest(testDispatcher) {
        val currentAccount = Account(id = 1, username = "me", fullName = "Me",
            email = "", password = "", imageUrl = "", bio = "", techStack = "",
            githubUrl = "", linkedInUrl = "", websiteUrl = "", points = 0, fcmToken = "",
            followerIds = "", followingIds = "")

        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(currentAccount)

        val viewModel = ViewQuestionsViewModel(questionRepository, accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isNotFollowingAnyone)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `toggle bookmark adds and removes questionId`() {
        val viewModel = createViewModel()
        viewModel.onAction(ViewQuestionsAction.ToggleBookmark(42))
        assertTrue(42 in viewModel.uiState.value.bookmarkedIds)

        viewModel.onAction(ViewQuestionsAction.ToggleBookmark(42))
        assertFalse(42 in viewModel.uiState.value.bookmarkedIds)
    }

    @Test
    fun `tab selected resets questions and reloads`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(
            Account(1, "", "", "", "", "", "", "", "", "", "", followingIds = "2")
        )
        coEvery { questionRepository.getByAccountIds(listOf(2), 0, 10) } returns Result.Success(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(ViewQuestionsAction.TabSelected(1))
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.selectedTab)
        assertEquals(0, viewModel.uiState.value.currentPage)
    }

    @Test
    fun `search query change updates state`() {
        val viewModel = createViewModel()
        viewModel.onAction(ViewQuestionsAction.SearchQueryChanged("kotlin"))
        assertEquals("kotlin", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `refresh reloads the feed`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(
            Account(1, "", "", "", "", "",
                "", "", "", "", "", followingIds = "2")
        )
        coEvery { questionRepository.getByAccountIds(any(), any(), any()) } returns Result.Success(emptyList())
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onAction(ViewQuestionsAction.Refresh)
        advanceUntilIdle()
        assertEquals(false, viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `accountId zero does not crash`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(0)

        val viewModel = ViewQuestionsViewModel(questionRepository, accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    private fun createViewModel(): ViewQuestionsViewModel {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(
            Account(1, "", "", "", "", "", "", "", "", "", "", followingIds = "")
        )
        return ViewQuestionsViewModel(questionRepository, accountRepository, userPreferencesRepository)
    }
}
