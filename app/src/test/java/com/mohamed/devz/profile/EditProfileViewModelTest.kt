package com.mohamed.devz.profile

import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileAction
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: AccountRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = mockk()
        userPreferencesRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads profile`() = runTest(testDispatcher) {
        val account = Account(id = 1, username = "johndoe", fullName = "John Doe",
            email = "john@test.com", password = "pwd", imageUrl = "https://example.com/avatar.jpg",
            bio = "Dev", techStack = "Kotlin,Compose", githubUrl = "https://github.com/johndoe",
            linkedInUrl = "", websiteUrl = "", points = 10, fcmToken = "", followerIds = "",
            followingIds = "")
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)

        val viewModel = EditProfileViewModel(accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertEquals("John Doe", viewModel.uiState.value.fullName)
        assertEquals("johndoe", viewModel.uiState.value.username)
        assertEquals("john@test.com", viewModel.uiState.value.email)
        assertEquals("Dev", viewModel.uiState.value.bio)
        assertEquals(listOf("Kotlin", "Compose"), viewModel.uiState.value.skills)
        assertEquals("https://github.com/johndoe", viewModel.uiState.value.github)
        assertEquals("https://example.com/avatar.jpg", viewModel.uiState.value.imageUrl)
    }

    @Test
    fun `field changes update state`() {
        val viewModel = createViewModel()
        viewModel.onAction(EditProfileAction.FullNameChanged("Jane"))
        viewModel.onAction(EditProfileAction.UsernameChanged("jane"))
        viewModel.onAction(EditProfileAction.BioChanged("New bio"))
        viewModel.onAction(EditProfileAction.GithubChanged("https://github.com/jane"))
        viewModel.onAction(EditProfileAction.LinkedinChanged("https://linkedin.com/in/jane"))
        viewModel.onAction(EditProfileAction.WebsiteChanged("https://jane.dev"))
        assertEquals("Jane", viewModel.uiState.value.fullName)
        assertEquals("jane", viewModel.uiState.value.username)
        assertEquals("New bio", viewModel.uiState.value.bio)
        assertEquals("https://github.com/jane", viewModel.uiState.value.github)
        assertEquals("https://linkedin.com/in/jane", viewModel.uiState.value.linkedin)
        assertEquals("https://jane.dev", viewModel.uiState.value.website)
    }

    @Test
    fun `add skill adds skill and clears input`() {
        val viewModel = createViewModel()
        viewModel.onAction(EditProfileAction.SkillInputChanged("Android"))
        viewModel.onAction(EditProfileAction.AddSkill)

        assertEquals(listOf("Android"), viewModel.uiState.value.skills)
        assertEquals("", viewModel.uiState.value.skillInput)
        assertFalse(viewModel.uiState.value.showSkillInput)
    }

    @Test
    fun `add skill ignores empty input`() {
        val viewModel = createViewModel()
        viewModel.onAction(EditProfileAction.AddSkill)
        assertTrue(viewModel.uiState.value.skills.isEmpty())
    }

    @Test
    fun `remove skill removes specified skill`() {
        val viewModel = createViewModel()
        viewModel.onAction(EditProfileAction.SkillInputChanged("Kotlin"))
        viewModel.onAction(EditProfileAction.AddSkill)
        viewModel.onAction(EditProfileAction.SkillInputChanged("Java"))
        viewModel.onAction(EditProfileAction.AddSkill)

        viewModel.onAction(EditProfileAction.RemoveSkill("Kotlin"))
        assertEquals(listOf("Java"), viewModel.uiState.value.skills)
    }

    @Test
    fun `toggle public profile flips the flag`() {
        val viewModel = createViewModel()
        val initial = viewModel.uiState.value.isPublicProfile
        viewModel.onAction(EditProfileAction.TogglePublicProfile)
        assertEquals(!initial, viewModel.uiState.value.isPublicProfile)
    }

    @Test
    fun `toggle display email flips the flag`() {
        val viewModel = createViewModel()
        val initial = viewModel.uiState.value.displayEmail
        viewModel.onAction(EditProfileAction.ToggleDisplayEmail)
        assertEquals(!initial, viewModel.uiState.value.displayEmail)
    }

    @Test
    fun `clear error sets error to null`() {
        val viewModel = createViewModel()
        viewModel.onAction(EditProfileAction.ClearError)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `save updates account and calls onSaved`() = runTest(testDispatcher) {
        val account = Account(id = 1, username = "johndoe", fullName = "John Doe",
            email = "john@test.com", password = "pwd", imageUrl = "", bio = "Bio",
            techStack = "Kotlin", githubUrl = "", linkedInUrl = "", websiteUrl = "")
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)
        coEvery { accountRepository.update(any()) } returns Result.Success(Unit)

        var onSavedCalled = false
        val viewModel = EditProfileViewModel(accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(EditProfileAction.Save { onSavedCalled = true })
        advanceUntilIdle()

        assertTrue(onSavedCalled)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `save with error shows error`() = runTest(testDispatcher) {
        val account = Account(id = 1, username = "johndoe", fullName = "John Doe",
            email = "john@test.com", password = "pwd", imageUrl = "", bio = "Bio",
            techStack = "Kotlin", githubUrl = "", linkedInUrl = "", websiteUrl = "")
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(account)
        coEvery { accountRepository.update(any()) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)

        val viewModel = EditProfileViewModel(accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        viewModel.onAction(EditProfileAction.Save { })
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `accountId zero shows user not found error`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(0)

        val viewModel = EditProfileViewModel(accountRepository, userPreferencesRepository)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    private fun createViewModel(): EditProfileViewModel {
        coEvery { userPreferencesRepository.observeCurrentAccountId() } returns MutableStateFlow(1)
        coEvery { accountRepository.getById(1) } returns Result.Success(
            Account(1, "", "", "", "", "", "", "", "", "", "")
        )
        return EditProfileViewModel(accountRepository, userPreferencesRepository)
    }
}
