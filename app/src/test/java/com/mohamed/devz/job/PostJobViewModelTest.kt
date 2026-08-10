package com.mohamed.devz.job

import com.mohamed.devz.feature.core.domain.model.CompanyProfile
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.job.presentation.post_job.PostJobAction
import com.mohamed.devz.feature.job.presentation.post_job.PostJobField
import com.mohamed.devz.feature.job.presentation.post_job.PostJobViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostJobViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var jobRepository: JobRepository
    private lateinit var companyProfileRepository: CompanyProfileRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        jobRepository = mockk()
        companyProfileRepository = mockk()
        userPreferencesRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = PostJobViewModel(
        jobRepository = jobRepository,
        companyProfileRepository = companyProfileRepository,
        userPreferencesRepository = userPreferencesRepository,
    )

    @Test
    fun `initial state cannot submit`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.canSubmit)
        assertNull(viewModel.uiState.value.titleError)
        assertNull(viewModel.uiState.value.descriptionError)
    }

    @Test
    fun `valid fields enable submit and clear errors`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged("We are hiring a developer for our mobile team."))

        assertTrue(viewModel.uiState.value.canSubmit)
        assertNull(viewModel.uiState.value.titleError)
        assertNull(viewModel.uiState.value.descriptionError)
    }

    @Test
    fun `blank title shows required error and blocks submit`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("   "))
        viewModel.onAction(PostJobAction.DescriptionChanged("We are hiring a developer for our mobile team."))

        assertEquals(UiText.DynamicString("Title is required"), viewModel.uiState.value.titleError)
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `short title shows min length error`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("De"))

        assertEquals(
            UiText.DynamicString("Title must be at least 3 characters"),
            viewModel.uiState.value.titleError,
        )
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `blank description shows required error and blocks submit`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged(" "))

        assertEquals(UiText.DynamicString("Description is required"), viewModel.uiState.value.descriptionError)
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `short description shows min length error`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged("Too short."))

        assertEquals(
            UiText.DynamicString("Description must be at least 20 characters"),
            viewModel.uiState.value.descriptionError,
        )
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `errors clear when fields become valid`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("De"))
        viewModel.onAction(PostJobAction.DescriptionChanged("short"))

        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged("We are hiring a developer for our mobile team."))

        assertNull(viewModel.uiState.value.titleError)
        assertNull(viewModel.uiState.value.descriptionError)
        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `focus loss marks field touched and computes required error`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.FieldFocusChanged(PostJobField.TITLE, false))

        assertTrue(viewModel.uiState.value.titleTouched)
        assertEquals(UiText.DynamicString("Title is required"), viewModel.uiState.value.titleError)
    }

    @Test
    fun `changed field is marked touched`() {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.DescriptionChanged("We are hiring a developer for our mobile team."))

        assertTrue(viewModel.uiState.value.descriptionTouched)
    }

    @Test
    fun `submit with invalid fields does not insert posting`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged("short"))
        viewModel.onAction(PostJobAction.Submit(onSuccess = { }))
        advanceUntilIdle()

        coVerify(exactly = 0) { jobRepository.insertJobPosting(any()) }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            UiText.DynamicString("Description must be at least 20 characters"),
            viewModel.uiState.value.descriptionError,
        )
    }

    @Test
    fun `submit with valid fields inserts posting and calls onSuccess`() = runTest(testDispatcher) {
        every { userPreferencesRepository.observeCurrentAccountId() } returns flowOf(7)
        coEvery { companyProfileRepository.getByAccountId(7) } returns Result.Success(
            CompanyProfile(userId = 7, companyName = "Acme")
        )
        coEvery { jobRepository.insertJobPosting(any()) } returns Result.Success(
            JobPosting(id = 1, companyName = "Acme", title = "Android Developer")
        )

        var onSuccessCalled = false
        val viewModel = createViewModel()
        viewModel.onAction(PostJobAction.TitleChanged("Android Developer"))
        viewModel.onAction(PostJobAction.DescriptionChanged("We are hiring a developer for our mobile team."))
        viewModel.onAction(PostJobAction.Submit(onSuccess = { onSuccessCalled = true }))
        advanceUntilIdle()

        coVerify(exactly = 1) { jobRepository.insertJobPosting(any()) }
        assertTrue(onSuccessCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
