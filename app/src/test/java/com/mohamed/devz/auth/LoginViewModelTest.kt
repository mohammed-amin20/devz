package com.mohamed.devz.auth

import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginAction
import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginViewModel
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.FcmTokenUtil
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.presentation.util.UiText
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: AccountRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = mockk()
        userPreferencesRepository = mockk()
        mockkObject(FcmTokenUtil)
        every { FcmTokenUtil.saveCurrentToken(any(), any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `username change updates state`() {
        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)
        viewModel.onAction(LoginAction.UsernameChanged("john"))
        assertEquals("john", viewModel.uiState.value.username)
    }

    @Test
    fun `password change updates state`() {
        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)
        viewModel.onAction(LoginAction.PasswordChanged("secret"))
        assertEquals("secret", viewModel.uiState.value.password)
    }

    @Test
    fun `login success saves preferences and calls onSuccess`() = runTest(testDispatcher) {
        val account = Account(id = 1, username = "john", fullName = "John", email = "john@test.com",
            password = "pwd", imageUrl = "", bio = "", techStack = "", githubUrl = "",
            linkedInUrl = "", websiteUrl = "")
        coEvery { accountRepository.getByUsernameAndPassword("john", "pass") } returns Result.Success(account)
        coEvery { userPreferencesRepository.setLoggedIn() } returns Result.Success(Unit)
        coEvery { userPreferencesRepository.setAccountId(any()) } returns Result.Success(Unit)

        var onSuccessCalled = false
        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(LoginAction.UsernameChanged("john"))
        viewModel.onAction(LoginAction.PasswordChanged("pass"))
        viewModel.onAction(LoginAction.LoginClicked(onSuccess = { onSuccessCalled = true }, onBanned = {}))
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with null account shows invalid credentials error`() = runTest(testDispatcher) {
        coEvery { accountRepository.getByUsernameAndPassword("john", "wrong") } returns Result.Success(null)

        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(LoginAction.UsernameChanged("john"))
        viewModel.onAction(LoginAction.PasswordChanged("wrong"))
        viewModel.onAction(LoginAction.LoginClicked(onSuccess = {}, onBanned = {}))
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        val error = viewModel.uiState.value.error
        assertNotNull(error)
        assertEquals("Invalid credentials", (error as UiText.DynamicString).value)
    }

    @Test
    fun `login with repository error shows error`() = runTest(testDispatcher) {
        coEvery { accountRepository.getByUsernameAndPassword("john", "pass") } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Network)

        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(LoginAction.UsernameChanged("john"))
        viewModel.onAction(LoginAction.PasswordChanged("pass"))
        viewModel.onAction(LoginAction.LoginClicked(onSuccess = {}, onBanned = {}))
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `login sets isLoading true while loading`() = runTest(testDispatcher) {
        coEvery { accountRepository.getByUsernameAndPassword(any(), any()) } returns Result.Success(null)

        val viewModel = LoginViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(LoginAction.UsernameChanged("john"))
        viewModel.onAction(LoginAction.PasswordChanged("pass"))
        viewModel.onAction(LoginAction.LoginClicked(onSuccess = {}, onBanned = {}))

        assertEquals(true, viewModel.uiState.value.isLoading)

        advanceUntilIdle()
    }
}
