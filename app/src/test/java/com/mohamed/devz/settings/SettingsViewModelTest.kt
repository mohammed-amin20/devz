package com.mohamed.devz.settings

import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.settings.presentation.AppLanguage
import com.mohamed.devz.feature.settings.presentation.SettingsAction
import com.mohamed.devz.feature.settings.presentation.SettingsEvent
import com.mohamed.devz.feature.settings.presentation.SettingsViewModel
import com.mohamed.devz.ui.theme.ThemeMode
import io.mockk.coEvery
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

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
    fun `init loads persisted theme and language into state`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeThemeMode() } returns
            MutableStateFlow(ThemeMode.LIGHT.storageKey)
        coEvery { userPreferencesRepository.observeLanguage() } returns
            MutableStateFlow(AppLanguage.ARABIC.storageKey)

        val viewModel = SettingsViewModel(userPreferencesRepository)
        advanceUntilIdle()

        assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.themeMode)
        assertEquals(AppLanguage.ARABIC, viewModel.uiState.value.language)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `logout clears preferences and emits NavigateToAuth`() = runTest(testDispatcher) {
        coEvery { userPreferencesRepository.observeThemeMode() } returns
            MutableStateFlow(ThemeMode.DARK.storageKey)
        coEvery { userPreferencesRepository.observeLanguage() } returns
            MutableStateFlow(AppLanguage.SYSTEM.storageKey)
        coEvery { userPreferencesRepository.setLoggedOut() } returns Result.Success(Unit)
        coEvery { userPreferencesRepository.clearAccountId() } returns Result.Success(Unit)

        val viewModel = SettingsViewModel(userPreferencesRepository)
        advanceUntilIdle()

        val events = mutableListOf<SettingsEvent>()
        val job = launch { viewModel.event.collect { events.add(it) } }

        viewModel.onAction(SettingsAction.Logout)
        advanceUntilIdle()
        job.cancel()

        assertEquals(1, events.size)
        assertEquals(SettingsEvent.NavigateToAuth, events[0])
    }
}