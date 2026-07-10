package com.mohamed.devz.auth

import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpAction
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpViewModel
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.FcmTokenUtil
import com.mohamed.devz.feature.core.domain.util.Result
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
class SignUpViewModelTest {

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
    fun `field changes update state`() {
        val viewModel = SignUpViewModel(accountRepository, userPreferencesRepository)
        viewModel.onAction(SignUpAction.FullNameChanged("John Doe"))
        viewModel.onAction(SignUpAction.UsernameChanged("johndoe"))
        viewModel.onAction(SignUpAction.EmailChanged("john@test.com"))
        viewModel.onAction(SignUpAction.PasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.ConfirmPasswordChanged("pass123"))

        assertEquals("John Doe", viewModel.uiState.value.fullName)
        assertEquals("johndoe", viewModel.uiState.value.username)
        assertEquals("john@test.com", viewModel.uiState.value.email)
        assertEquals("pass123", viewModel.uiState.value.password)
        assertEquals("pass123", viewModel.uiState.value.confirmPassword)
    }

    @Test
    fun `password mismatch shows error and does not register`() {
        val viewModel = SignUpViewModel(accountRepository, userPreferencesRepository)
        viewModel.onAction(SignUpAction.PasswordChanged("pass1"))
        viewModel.onAction(SignUpAction.ConfirmPasswordChanged("pass2"))
        viewModel.onAction(SignUpAction.RegisterClicked { })

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `register success saves preferences and calls onSuccess`() = runTest(testDispatcher) {
        val createdAccount = Account(id = 5, username = "johndoe", fullName = "John Doe",
            email = "john@test.com", password = "pass123", imageUrl = "", bio = "",
            techStack = "", githubUrl = "", linkedInUrl = "", websiteUrl = "")
        coEvery { accountRepository.insert(any()) } returns Result.Success(createdAccount)
        coEvery { userPreferencesRepository.setLoggedIn() } returns Result.Success(Unit)
        coEvery { userPreferencesRepository.setAccountId(any()) } returns Result.Success(Unit)

        var onSuccessCalled = false
        val viewModel = SignUpViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(SignUpAction.FullNameChanged("John Doe"))
        viewModel.onAction(SignUpAction.UsernameChanged("johndoe"))
        viewModel.onAction(SignUpAction.EmailChanged("john@test.com"))
        viewModel.onAction(SignUpAction.PasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.ConfirmPasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.RegisterClicked { onSuccessCalled = true })
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `register with repository error shows error`() = runTest(testDispatcher) {
        coEvery { accountRepository.insert(any()) } returns
            Result.Error(com.mohamed.devz.feature.core.domain.util.Error.Conflict)

        val viewModel = SignUpViewModel(accountRepository, userPreferencesRepository)

        viewModel.onAction(SignUpAction.FullNameChanged("John Doe"))
        viewModel.onAction(SignUpAction.UsernameChanged("johndoe"))
        viewModel.onAction(SignUpAction.EmailChanged("john@test.com"))
        viewModel.onAction(SignUpAction.PasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.ConfirmPasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.RegisterClicked { })
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `register sets isLoading true while loading`() = runTest(testDispatcher) {
        coEvery { accountRepository.insert(any()) } returns
            Result.Success(Account(0, "", "", "", "", "", "", "", "", "", ""))
        coEvery { userPreferencesRepository.setLoggedIn() } returns Result.Success(Unit)
        coEvery { userPreferencesRepository.setAccountId(any()) } returns Result.Success(Unit)

        val viewModel = SignUpViewModel(accountRepository, userPreferencesRepository)
        viewModel.onAction(SignUpAction.FullNameChanged("John Doe"))
        viewModel.onAction(SignUpAction.UsernameChanged("johndoe"))
        viewModel.onAction(SignUpAction.EmailChanged("john@test.com"))
        viewModel.onAction(SignUpAction.PasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.ConfirmPasswordChanged("pass123"))
        viewModel.onAction(SignUpAction.RegisterClicked { })

        assertEquals(true, viewModel.uiState.value.isLoading)

        advanceUntilIdle()
    }
}
