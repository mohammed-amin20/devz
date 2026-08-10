package com.mohamed.devz.job

import com.mohamed.devz.feature.core.domain.model.JobApplication
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.repository.JobRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.job.presentation.job_detail.JobApplicationField
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailAction
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JobDetailViewModelTest {

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

    private fun createViewModel() = JobDetailViewModel(
        jobRepository = jobRepository,
        companyProfileRepository = companyProfileRepository,
        userPreferencesRepository = userPreferencesRepository,
    )

    private fun loadJob(viewModel: JobDetailViewModel, jobId: Int = 1) = runTest(testDispatcher) {
        coEvery { jobRepository.getJobPostingById(jobId) } returns Result.Success(
            JobPosting(id = jobId, companyName = "Acme", title = "Android Dev")
        )
        coEvery { jobRepository.getApplicationsByJobId(jobId) } returns Result.Success(emptyList())
        coEvery { companyProfileRepository.getByAccountId(any()) } returns Result.Success(null)
        every { userPreferencesRepository.observeCurrentAccountId() } returns flowOf(7)
        coEvery { jobRepository.getApplicationsByApplicantId(7) } returns Result.Success(emptyList())

        viewModel.onAction(JobDetailAction.LoadJob(jobId))
        advanceUntilIdle()
    }

    @Test
    fun `initial state cannot submit`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.canSubmit)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.whatsappError)
        assertNull(viewModel.uiState.value.coverLetterError)
    }

    @Test
    fun `valid fields enable submit and clear errors`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))

        assertTrue(viewModel.uiState.value.canSubmit)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.whatsappError)
        assertNull(viewModel.uiState.value.coverLetterError)
    }

    @Test
    fun `invalid email shows inline error and blocks submit`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("not-an-email"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))

        assertEquals(UiText.DynamicString("Invalid email format"), viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `blank email shows required error`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("   "))

        assertEquals(UiText.DynamicString("Email is required"), viewModel.uiState.value.emailError)
    }

    @Test
    fun `invalid whatsapp shows inline error`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.WhatsAppChanged("12"))
        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))

        assertEquals(
            UiText.DynamicString("Enter a valid WhatsApp number (8-15 digits, optional +)"),
            viewModel.uiState.value.whatsappError,
        )
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `whatsapp with letters shows inline error`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+12345abcde"))

        assertNotNull(viewModel.uiState.value.whatsappError)
    }

    @Test
    fun `short cover letter shows inline error`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.CoverLetterChanged("Too short"))

        assertEquals(
            UiText.DynamicString("Cover letter must be at least 20 characters"),
            viewModel.uiState.value.coverLetterError,
        )
        assertFalse(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `cover letter length counts after trimming whitespace`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.CoverLetterChanged("        "))

        assertEquals(
            UiText.DynamicString("Cover letter is required"),
            viewModel.uiState.value.coverLetterError,
        )
    }

    @Test
    fun `errors clear when field becomes valid`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("not-an-email"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("123"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("short"))

        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))

        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.whatsappError)
        assertNull(viewModel.uiState.value.coverLetterError)
        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `focus loss marks field touched and computes required error`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.FieldFocusChanged(JobApplicationField.EMAIL, false))

        assertTrue(viewModel.uiState.value.emailTouched)
        assertEquals(UiText.DynamicString("Email is required"), viewModel.uiState.value.emailError)
    }

    @Test
    fun `changed field is marked touched`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))

        assertTrue(viewModel.uiState.value.emailTouched)
    }

    @Test
    fun `showApplySheet resets touched flags but keeps validity`() {
        val viewModel = createViewModel()
        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))
        assertTrue(viewModel.uiState.value.canSubmit)

        viewModel.showApplySheet()

        assertFalse(viewModel.uiState.value.emailTouched)
        assertFalse(viewModel.uiState.value.whatsappTouched)
        assertFalse(viewModel.uiState.value.coverLetterTouched)
        assertTrue(viewModel.uiState.value.canSubmit)
        assertTrue(viewModel.uiState.value.showApplySheet)
    }

    @Test
    fun `submit with invalid fields does not insert application`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        loadJob(viewModel)

        viewModel.onAction(JobDetailAction.EmailChanged("not-an-email"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))
        viewModel.onAction(JobDetailAction.SubmitApplication(onSuccess = { }))
        advanceUntilIdle()

        coVerify(exactly = 0) { jobRepository.insertApplication(any()) }
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `submit with valid fields inserts application and resets form`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        loadJob(viewModel)

        coEvery { jobRepository.insertApplication(any()) } returns Result.Success(
            JobApplication(id = 1, jobId = 1, applicantId = 7, coverLetter = "x", email = "john@test.com", whatsapp = "+123456789")
        )

        var onSuccessCalled = false
        viewModel.onAction(JobDetailAction.EmailChanged("john@test.com"))
        viewModel.onAction(JobDetailAction.WhatsAppChanged("+123456789"))
        viewModel.onAction(JobDetailAction.CoverLetterChanged("I am a strong candidate for this role."))
        viewModel.onAction(JobDetailAction.SubmitApplication(onSuccess = { onSuccessCalled = true }))
        advanceUntilIdle()

        coVerify(exactly = 1) { jobRepository.insertApplication(any()) }
        assertTrue(onSuccessCalled)
        assertTrue(viewModel.uiState.value.hasApplied)
        assertFalse(viewModel.uiState.value.canSubmit)
        assertEquals("", viewModel.uiState.value.email)
    }
}
