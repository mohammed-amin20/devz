package com.mohamed.devz.splash

import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.splash.presentation.SplashAction
import com.mohamed.devz.feature.splash.presentation.SplashViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `first time user navigates to onboarding`() = runTest(testDispatcher) {
        every { userPreferencesRepository.observeIsFirstTime() } returns MutableStateFlow(true)
        every { userPreferencesRepository.observeIsLoggedIn() } returns MutableStateFlow(false)

        val viewModel = SplashViewModel(userPreferencesRepository)
        val events = mutableListOf<SplashViewModel.SplashEvent>()
        val job = launch { viewModel.splashEvent.collect { events.add(it) } }

        viewModel.onAction(SplashAction.DecideWhereToGoAction)
        advanceUntilIdle()
        job.cancel()

        assertEquals(1, events.size)
        assertEquals(SplashViewModel.SplashEvent.NavigateToOnboarding, events[0])
    }

    @Test
    fun `not first time and not logged in navigates to auth`() = runTest(testDispatcher) {
        every { userPreferencesRepository.observeIsFirstTime() } returns MutableStateFlow(false)
        every { userPreferencesRepository.observeIsLoggedIn() } returns MutableStateFlow(false)

        val viewModel = SplashViewModel(userPreferencesRepository)
        val events = mutableListOf<SplashViewModel.SplashEvent>()
        val job = launch { viewModel.splashEvent.collect { events.add(it) } }

        viewModel.onAction(SplashAction.DecideWhereToGoAction)
        advanceUntilIdle()
        job.cancel()

        assertEquals(1, events.size)
        assertEquals(SplashViewModel.SplashEvent.NavigateToAuth, events[0])
    }

    @Test
    fun `logged in user navigates to home`() = runTest(testDispatcher) {
        every { userPreferencesRepository.observeIsFirstTime() } returns MutableStateFlow(false)
        every { userPreferencesRepository.observeIsLoggedIn() } returns MutableStateFlow(true)

        val viewModel = SplashViewModel(userPreferencesRepository)
        val events = mutableListOf<SplashViewModel.SplashEvent>()
        val job = launch { viewModel.splashEvent.collect { events.add(it) } }

        viewModel.onAction(SplashAction.DecideWhereToGoAction)
        advanceUntilIdle()
        job.cancel()

        assertEquals(1, events.size)
        assertEquals(SplashViewModel.SplashEvent.NavigateToHome, events[0])
    }
}
