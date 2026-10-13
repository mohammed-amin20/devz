package com.mohamed.devz.gallery

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import com.mohamed.devz.feature.admin.presentation.admin_dashboard.AdminDashboardContent
import com.mohamed.devz.feature.admin.presentation.admin_dashboard.AdminDashboardState
import com.mohamed.devz.feature.admin.presentation.manage_announcements.ManageAnnouncementsContent
import com.mohamed.devz.feature.admin.presentation.manage_announcements.ManageAnnouncementsState
import com.mohamed.devz.feature.admin.presentation.manage_answers.ManageAnswersContent
import com.mohamed.devz.feature.admin.presentation.manage_answers.ManageAnswersState
import com.mohamed.devz.feature.admin.presentation.manage_companies.CompanyUiModel
import com.mohamed.devz.feature.admin.presentation.manage_companies.ManageCompaniesContent
import com.mohamed.devz.feature.admin.presentation.manage_companies.ManageCompaniesState
import com.mohamed.devz.feature.admin.presentation.manage_jobs.ManageJobsContent
import com.mohamed.devz.feature.admin.presentation.manage_jobs.ManageJobsState
import com.mohamed.devz.feature.admin.presentation.manage_questions.ManageQuestionsContent
import com.mohamed.devz.feature.admin.presentation.manage_questions.ManageQuestionsState
import com.mohamed.devz.feature.admin.presentation.manage_reports.ManageReportsContent
import com.mohamed.devz.feature.admin.presentation.manage_reports.ManageReportsState
import com.mohamed.devz.feature.admin.presentation.manage_reports.ReportUiModel
import com.mohamed.devz.feature.admin.presentation.manage_users.ManageUsersContent
import com.mohamed.devz.feature.admin.presentation.manage_users.ManageUsersState
import com.mohamed.devz.feature.authentication.presentation.BannedScreen
import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginContent
import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginState
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpContent
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpState
import com.mohamed.devz.feature.company.presentation.PendingApprovalScreen
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyDashboardContent
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyDashboardState
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyJobUiModel
import com.mohamed.devz.feature.company.presentation.company_job_detail.CompanyJobDetailContent
import com.mohamed.devz.feature.company.presentation.company_job_detail.CompanyJobDetailState
import com.mohamed.devz.feature.company.presentation.company_job_detail.ProposalUiModel
import com.mohamed.devz.feature.company.presentation.company_profile.CompanyProfileContent
import com.mohamed.devz.feature.company.presentation.company_profile.CompanyProfileUiState
import com.mohamed.devz.feature.company.presentation.edit_company_profile.EditCompanyProfileContent
import com.mohamed.devz.feature.company.presentation.edit_company_profile.EditCompanyProfileState
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.model.JobPosting
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailContent
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailState
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailUiModel
import com.mohamed.devz.feature.job.presentation.post_job.PostJobContent
import com.mohamed.devz.feature.job.presentation.post_job.PostJobState
import com.mohamed.devz.feature.notification.presentation.NotificationType
import com.mohamed.devz.feature.notification.presentation.NotificationUiModel
import com.mohamed.devz.feature.notification.presentation.NotificationsContent
import com.mohamed.devz.feature.notification.presentation.NotificationsUiState
import com.mohamed.devz.feature.onboarding.presentation.OnboardingContent
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileContent
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileState
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileContent
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileState
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileUiModel
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionContent
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionState
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailContent
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailUiModel
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailsState
import com.mohamed.devz.feature.question.presentation.question_details.components.AnswerUiModel
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsContent
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsState
import com.mohamed.devz.feature.question.presentation.view_questions.util.QuestionFeedUiModel
import com.mohamed.devz.feature.settings.presentation.AppLanguage
import com.mohamed.devz.feature.settings.presentation.SettingsContent
import com.mohamed.devz.feature.splash.presentation.SplashContent
import com.mohamed.devz.ui.components.BrandBackground
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders every distinct full-screen state in both dark and light themes and
 * writes real PNGs to `docs/screenshots/<name>-content-{dark,light}.png`.
 *
 * One fresh composition per theme per test to guarantee the light frame is
 * actually recomposed (toggling theme mid-composition left both frames dark).
 *
 * `HomeScreen` is intentionally excluded: its content embeds Hilt-backed
 * screens (`ViewQuestionsScreen`, `NotificationsScreen`, `JobsScreen`,
 * `ProfileScreen`) which cannot be rendered in a plain Robolectric test.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    application = GalleryTestApp::class,
    sdk = [34],
    qualifiers = "w411dp-h891dp-420dpi",
)
class ScreenshotGalleryTest {

    @get:Rule
    val compose = createComposeRule()

    private fun capture(
        name: String,
        themeMode: ThemeMode,
        ui: @Composable () -> Unit,
        selectedTab: Int? = null,
    ) {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            DevzTheme(themeMode = themeMode) {
                if (selectedTab == null) {
                    ui()
                } else {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = { TestBottomBar(selectedIndex = selectedTab) },
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            ui()
                        }
                    }
                }
            }
        }
        compose.mainClock.advanceTimeBy(1_000)
        compose.onRoot().captureRoboImage(
            "../docs/screenshots/$name-content-${themeMode.storageKey}.png",
        )
    }

    /**
     * Test-only replica of the app's `HomeContent` bottom navigation bar so tab
     * screenshots include the shell chrome. Mirrors HomeScreen.kt: it is NOT the
     * production composable (that one is private and Hilt-bound), just a visual copy.
     */
    @Composable
    private fun TestBottomBar(selectedIndex: Int, unreadCount: Int = 3) {
        val icons = listOf(
            Icons.Rounded.Home,
            Icons.Rounded.Add,
            Icons.Rounded.Notifications,
            Icons.Rounded.WorkHistory,
            Icons.Rounded.Person,
        )
        val scrimColor = MaterialTheme.colorScheme.scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, scrimColor.copy(alpha = 0.5f)),
                            startY = 0f,
                            endY = size.height,
                        )
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .height(64.dp)
                    .imePadding()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(0, 2).forEach { index ->
                    TestNavIcon(
                        icon = icons[index],
                        isSelected = index == selectedIndex,
                        badgeCount = if (index == 2) unreadCount else 0,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.width(76.dp))
                listOf(3, 4).forEach { index ->
                    TestNavIcon(
                        icon = icons[index],
                        isSelected = index == selectedIndex,
                        badgeCount = 0,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            TestAddFab(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-4).dp),
            )
        }
    }

    @Composable
    private fun TestNavIcon(icon: ImageVector, isSelected: Boolean, badgeCount: Int, modifier: Modifier = Modifier) {
        val tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f).toArgb()
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(48.dp)
                .clip(RoundedCornerShape(50))
                .drawBehind {
                    if (isSelected) {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                color = android.graphics.Color.TRANSPARENT
                                setShadowLayer(16f, 0f, 0f, glowColor)
                            }
                            canvas.nativeCanvas.drawCircle(center.x, center.y, 20.dp.toPx(), paint)
                        }
                    }
                }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(26.dp),
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

    @Composable
    private fun TestAddFab(modifier: Modifier = Modifier) {
        val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(60.dp)
                .shadow(16.dp, CircleShape, clip = false, ambientColor = glowColor, spotColor = glowColor)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF00BCD4), Color(0xFF44D8F1))))
                .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = Color(0xFF00363E),
                modifier = Modifier.size(32.dp),
            )
        }
    }

    // ── Authentication ──────────────────────────────────────────────────────

    @Test fun loginDark() = capture("login", ThemeMode.DARK, loginUi)
    @Test fun loginLight() = capture("login", ThemeMode.LIGHT, loginUi)

    @Test fun signupDark() = capture("signup", ThemeMode.DARK, signupUi)
    @Test fun signupLight() = capture("signup", ThemeMode.LIGHT, signupUi)

    @Test fun bannedDark() = capture("banned", ThemeMode.DARK, bannedUi)
    @Test fun bannedLight() = capture("banned", ThemeMode.LIGHT, bannedUi)

    // ── Onboarding flow ──────────────────────────────────────────────────────

    @Test fun splashDark() = capture("splash", ThemeMode.DARK, splashUi)
    @Test fun splashLight() = capture("splash", ThemeMode.LIGHT, splashUi)

    @Test fun onboardingDark() = capture("onboarding", ThemeMode.DARK, onboardingUi)
    @Test fun onboardingLight() = capture("onboarding", ThemeMode.LIGHT, onboardingUi)

    // ── Feed ─────────────────────────────────────────────────────────────────

    @Test fun feedDark() = capture("feed", ThemeMode.DARK, feedUi, selectedTab = 0)
    @Test fun feedLight() = capture("feed", ThemeMode.LIGHT, feedUi, selectedTab = 0)

    // ── Question detail / editor ─────────────────────────────────────────────

    @Test fun questionDetailsDark() = capture("question-details", ThemeMode.DARK, questionDetailsUi)
    @Test fun questionDetailsLight() = capture("question-details", ThemeMode.LIGHT, questionDetailsUi)

    @Test fun addEditQuestionDark() = capture("add-edit-question", ThemeMode.DARK, addEditQuestionUi)
    @Test fun addEditQuestionLight() = capture("add-edit-question", ThemeMode.LIGHT, addEditQuestionUi)

    // ── Profile ──────────────────────────────────────────────────────────────

    @Test fun profileDark() = capture("profile", ThemeMode.DARK, profileUi, selectedTab = 4)
    @Test fun profileLight() = capture("profile", ThemeMode.LIGHT, profileUi, selectedTab = 4)

    @Test fun adminProfileDark() = capture("admin-profile", ThemeMode.DARK, adminProfileUi, selectedTab = 4)
    @Test fun adminProfileLight() = capture("admin-profile", ThemeMode.LIGHT, adminProfileUi, selectedTab = 4)

    @Test fun editProfileDark() = capture("edit-profile", ThemeMode.DARK, editProfileUi)
    @Test fun editProfileLight() = capture("edit-profile", ThemeMode.LIGHT, editProfileUi)

    @Test fun settingsDark() = capture("settings", ThemeMode.DARK, settingsUi)
    @Test fun settingsLight() = capture("settings", ThemeMode.LIGHT, settingsUi)

    // ── Notifications ────────────────────────────────────────────────────────

    @Test fun notificationsDark() = capture("notifications", ThemeMode.DARK, notificationsUi, selectedTab = 2)
    @Test fun notificationsLight() = capture("notifications", ThemeMode.LIGHT, notificationsUi, selectedTab = 2)

    // ── Jobs ─────────────────────────────────────────────────────────────────

    @Test fun jobDetailDark() = capture("job-detail", ThemeMode.DARK, jobDetailUi)
    @Test fun jobDetailLight() = capture("job-detail", ThemeMode.LIGHT, jobDetailUi)

    @Test fun postJobDark() = capture("post-job", ThemeMode.DARK, postJobUi)
    @Test fun postJobLight() = capture("post-job", ThemeMode.LIGHT, postJobUi)

    @Test fun pendingApprovalDark() = capture("pending-approval", ThemeMode.DARK, pendingApprovalUi)
    @Test fun pendingApprovalLight() = capture("pending-approval", ThemeMode.LIGHT, pendingApprovalUi)

    // ── Company ──────────────────────────────────────────────────────────────

    @Test fun companyDashboardDark() = capture("company-dashboard", ThemeMode.DARK, companyDashboardUi)
    @Test fun companyDashboardLight() = capture("company-dashboard", ThemeMode.LIGHT, companyDashboardUi)

    @Test fun companyProfileDark() = capture("company-profile", ThemeMode.DARK, companyProfileUi)
    @Test fun companyProfileLight() = capture("company-profile", ThemeMode.LIGHT, companyProfileUi)

    @Test fun companyJobDetailDark() = capture("company-job-detail", ThemeMode.DARK, companyJobDetailUi)
    @Test fun companyJobDetailLight() = capture("company-job-detail", ThemeMode.LIGHT, companyJobDetailUi)

    @Test fun editCompanyProfileDark() = capture("edit-company-profile", ThemeMode.DARK, editCompanyProfileUi)
    @Test fun editCompanyProfileLight() = capture("edit-company-profile", ThemeMode.LIGHT, editCompanyProfileUi)

    // ── Admin ────────────────────────────────────────────────────────────────

    @Test fun adminDashboardDark() = capture("admin-dashboard", ThemeMode.DARK, adminDashboardUi)
    @Test fun adminDashboardLight() = capture("admin-dashboard", ThemeMode.LIGHT, adminDashboardUi)

    @Test fun manageUsersDark() = capture("manage-users", ThemeMode.DARK, manageUsersUi)
    @Test fun manageUsersLight() = capture("manage-users", ThemeMode.LIGHT, manageUsersUi)

    @Test fun manageQuestionsDark() = capture("manage-questions", ThemeMode.DARK, manageQuestionsUi)
    @Test fun manageQuestionsLight() = capture("manage-questions", ThemeMode.LIGHT, manageQuestionsUi)

    @Test fun manageAnswersDark() = capture("manage-answers", ThemeMode.DARK, manageAnswersUi)
    @Test fun manageAnswersLight() = capture("manage-answers", ThemeMode.LIGHT, manageAnswersUi)

    @Test fun manageAnnouncementsDark() = capture("manage-announcements", ThemeMode.DARK, manageAnnouncementsUi)
    @Test fun manageAnnouncementsLight() = capture("manage-announcements", ThemeMode.LIGHT, manageAnnouncementsUi)

    @Test fun manageJobsDark() = capture("manage-jobs", ThemeMode.DARK, manageJobsUi)
    @Test fun manageJobsLight() = capture("manage-jobs", ThemeMode.LIGHT, manageJobsUi)

    @Test fun manageCompaniesDark() = capture("manage-companies", ThemeMode.DARK, manageCompaniesUi)
    @Test fun manageCompaniesLight() = capture("manage-companies", ThemeMode.LIGHT, manageCompaniesUi)

    @Test fun manageReportsDark() = capture("manage-reports", ThemeMode.DARK, manageReportsUi)
    @Test fun manageReportsLight() = capture("manage-reports", ThemeMode.LIGHT, manageReportsUi)
}

// ── Screen content (mirrors the private fixtures in each screen's @Preview) ──

private val loginUi: @Composable () -> Unit = {
    BrandBackground {
        LoginContent(
            state = LoginState(),
            onNavigateToRegister = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
        )
    }
}

private val signupUi: @Composable () -> Unit = {
    BrandBackground(accentCenter = Offset(0.65f, 0.58f)) {
        SignUpContent(
            state = SignUpState(),
            onNavigateToLogin = {},
            onCompanyNameChanged = {},
            onFullNameChanged = {},
            onUsernameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onIsCompanyToggled = {},
            onRegisterClicked = {},
        )
    }
}

private val bannedUi: @Composable () -> Unit = { BannedScreen() }

private val splashUi: @Composable () -> Unit = { SplashContent(onAnimationFinished = {}) }

private val onboardingUi: @Composable () -> Unit = { OnboardingContent(onFinish = {}) }

private val feedUi: @Composable () -> Unit = {
    ViewQuestionsContent(
        state = ViewQuestionsState(
            questions = listOf(previewQuestion, previewQuestion2),
            pinnedQuestions = listOf(previewQuestionPinned),
            isLoading = false,
            currentAccountId = 2,
        ),
        pagerState = rememberPagerState(pageCount = { 2 }),
        coroutineScope = rememberCoroutineScope(),
        onSearchQueryChanged = {},
        onTabSelected = {},
        onLoadNextPage = {},
        onRefresh = {},
        onQuestionClick = {},
        onAuthorClick = {},
        onShowReport = {},
        onDismissReport = {},
    )
}

private val questionDetailsUi: @Composable () -> Unit = {
    QuestionDetailContent(
        state = QuestionDetailsState(
            question = previewQuestionDetail,
            answers = listOf(previewAnswerUi),
            isLoading = false,
            currentAccountId = 5,
        ),
        questionId = 1,
        navigateUp = {},
        snackbarHostState = SnackbarHostState(),
        onNavigateToProfile = {},
        onEditQuestion = {},
        onRetryLoad = {},
        onLikeClick = {},
        onAnswerVoteClick = {},
        onAcceptAnswer = {},
        onCodeLongPress = {},
        onShowReport = {},
        onDismissReport = {},
        onPinClick = {},
        onAnswerTextChange = {},
        onPostAnswer = {},
        onCodeEditorClick = {},
        onCodeChange = {},
        onCodeDone = {},
        onCodeClear = {},
    )
}

private val addEditQuestionUi: @Composable () -> Unit = {
    AddEditQuestionContent(
        state = AddEditQuestionState(
            title = "How to handle deep links in Compose Navigation?",
            body = "I'm trying to navigate to a specific screen using a deep link but it keeps opening the Home screen.",
            tags = listOf("compose", "navigation"),
        ),
        isEdit = false,
        navigateUp = {},
        onTitleChanged = {},
        onBodyChanged = {},
        onLanguageSelected = {},
        onCodeChanged = {},
        onTagInputChanged = {},
        onAddTag = {},
        onRemoveTag = {},
        onShowTagInput = {},
        onPublish = {},
    )
}

private val profileUi: @Composable () -> Unit = {
    ProfileContent(
        state = sampleProfileState,
        showImagePreview = false,
        onImagePreviewChange = {},
        onRefresh = {},
        onEditProfile = {},
        onQuestionClick = {},
        onToggleFollow = {},
        onShowFollowers = {},
        onShowFollowing = {},
        onDismissDialog = {},
        onShowReport = {},
        onDismissReport = {},
        onFullScreenChanged = {},
        navigateUp = {},
    )
}

private val adminProfileUi: @Composable () -> Unit = {
    ProfileContent(
        state = adminProfileState,
        showImagePreview = false,
        onImagePreviewChange = {},
        onRefresh = {},
        onEditProfile = {},
        onQuestionClick = {},
        onToggleFollow = {},
        onShowFollowers = {},
        onShowFollowing = {},
        onDismissDialog = {},
        onShowReport = {},
        onDismissReport = {},
        onFullScreenChanged = {},
        onAdminPanelClick = {},
        navigateUp = {},
    )
}

private val editProfileUi: @Composable () -> Unit = {
    EditProfileContent(
        state = EditProfileState(
            fullName = "John Doe",
            username = "johndoe",
            bio = "Android developer passionate about Compose.",
            skills = listOf("Kotlin", "Compose", "KMP"),
            github = "https://github.com/johndoe",
            linkedin = "https://linkedin.com/in/johndoe",
            website = "https://johndoe.dev",
        ),
        onNavigateUp = {},
        onSave = {},
        onPickImage = {},
        onClearError = {},
        onFullNameChanged = {},
        onUsernameChanged = {},
        onBioChanged = {},
        onAddSkill = {},
        onRemoveSkill = {},
        onShowSkillInput = {},
        onSkillInputChanged = {},
        onGithubChanged = {},
        onLinkedinChanged = {},
        onWebsiteChanged = {},
        onTogglePublicProfile = {},
        onToggleDisplayEmail = {},
        onDeactivateAccount = {},
    )
}

private val settingsUi: @Composable () -> Unit = {
    SettingsContent(
        themeMode = ThemeMode.DARK,
        language = AppLanguage.SYSTEM,
        onNavigateUp = {},
        onSelectThemeMode = {},
        onSelectLanguage = {},
        onLogoutConfirm = {},
    )
}

private val notificationsUi: @Composable () -> Unit = {
    NotificationsContent(
        state = NotificationsUiState(
            notifications = listOf(
                NotificationUiModel(
                    id = "1",
                    type = NotificationType.ANSWER,
                    message = "answered your question",
                    actorName = "Sara",
                    questionId = 10,
                    questionTitle = "How does Flutter handle hot reload?",
                    timeAgo = "2h ago",
                    isRead = false,
                    senderType = "user",
                ),
                NotificationUiModel(
                    id = "2",
                    type = NotificationType.UPVOTE,
                    message = "upvoted your answer",
                    actorName = null,
                    questionId = 11,
                    questionTitle = "Best practices for Jetpack Compose",
                    timeAgo = "1d ago",
                    isRead = true,
                    senderType = "user",
                ),
                NotificationUiModel(
                    id = "3",
                    type = NotificationType.SYSTEM,
                    message = "devZ maintenance\nScheduled maintenance on Sunday at 02:00 AM.",
                    actorName = null,
                    questionId = 0,
                    questionTitle = "",
                    timeAgo = "3d ago",
                    isRead = true,
                    senderType = "system",
                ),
            ),
        ),
    )
}

private val jobDetailUi: @Composable () -> Unit = {
    JobDetailContent(
        state = JobDetailState(
            job = JobDetailUiModel(
                id = 1,
                accountId = 1,
                companyName = "Acme Labs",
                title = "Senior Android Developer",
                description = "Build great things.",
                salaryRange = "$80k - $120k",
                jobType = "full-time",
                createdAt = "2024-01-01T00:00:00Z",
                status = "approved",
                applicantCount = 3,
            ),
            currentAccountId = 2,
        ),
        onNavigateUp = {},
        onProfileClick = {},
        onRetry = {},
        onShowReport = {},
        onApply = {},
    )
}

private val postJobUi: @Composable () -> Unit = {
    PostJobContent(
        state = PostJobState(
            title = "Senior Android Developer",
            description = "We are looking for a senior Android developer.",
            salaryRange = "$80k - $120k",
            jobType = "full-time",
        ),
        onNavigateUp = {},
        onTitleChanged = {},
        onDescriptionChanged = {},
        onSalaryRangeChanged = {},
        onJobTypeSelected = {},
        onFieldFocusChanged = { _, _ -> },
        onSubmit = {},
    )
}

private val pendingApprovalUi: @Composable () -> Unit = { PendingApprovalScreen(onGoToHome = {}) }

private val companyDashboardUi: @Composable () -> Unit = {
    CompanyDashboardContent(
        state = previewDashboardState,
        onRefresh = {},
        onPostJob = {},
        onJobClick = {},
        onLogout = {},
    )
}

private val companyProfileUi: @Composable () -> Unit = {
    CompanyProfileContent(
        state = CompanyProfileUiState(
            companyName = "Acme Labs",
            description = "Building awesome developer tools since 2021.",
            location = "Amman, Jordan",
            industry = "Software",
            website = "https://acme.dev",
            isVerified = true,
            isOwnProfile = true,
            postedJobs = listOf(
                CompanyJobUiModel(
                    id = 1,
                    title = "Senior Android Developer",
                    status = "approved",
                    createdAt = "2024-01-01T00:00:00Z",
                )
            ),
            totalJobs = 1,
            totalApplicants = 5,
            selectedTab = 0,
            isLoading = false,
        ),
        onJobClick = {},
        onPostJob = {},
        onBack = {},
        onRefresh = {},
        onSelectTab = {},
    )
}

private val companyJobDetailUi: @Composable () -> Unit = {
    CompanyJobDetailContent(
        state = CompanyJobDetailState(
            job = JobPosting(
                id = 1,
                companyName = "Acme Labs",
                title = "Senior Android Developer",
                description = "Build great things.",
                salaryRange = "$80k - $120k",
                jobType = "full-time",
            ),
            proposals = listOf(previewProposal),
            isLoading = false,
        ),
        onNavigateUp = {},
        onProfileClick = {},
        onRetry = {},
        onApprove = { _, _ -> },
    )
}

private val editCompanyProfileUi: @Composable () -> Unit = {
    EditCompanyProfileContent(
        state = EditCompanyProfileState(
            companyName = "Acme Labs",
            industry = "Software",
            location = "Amman, Jordan",
            website = "https://acme.dev",
            twitterUrl = "https://twitter.com/acme",
            description = "We build developer tools.",
            bio = "Hiring great engineers.",
            isLoading = false,
        ),
        onDismiss = {},
        onPickImage = {},
        onSave = {},
        onCompanyNameChanged = {},
        onIndustryChanged = {},
        onLocationChanged = {},
        onWebsiteChanged = {},
        onTwitterChanged = {},
        onDescriptionChanged = {},
        onBioChanged = {},
    )
}

private val adminDashboardUi: @Composable () -> Unit = {
    AdminDashboardContent(
        state = AdminDashboardState(
            totalUsers = 120,
            totalQuestions = 340,
            totalAnswers = 890,
            bannedUsers = 3,
            isLoading = false,
        ),
        onNavigateToUsers = {},
        onNavigateToQuestions = {},
        onNavigateToAnswers = {},
        onNavigateToAnnouncements = {},
        onNavigateUp = {},
    )
}

private val manageUsersUi: @Composable () -> Unit = {
    ManageUsersContent(
        state = ManageUsersState(
            filteredAccounts = listOf(previewAccount),
            selectedFilter = 0,
            isLoading = false,
        ),
        onNavigateUp = {},
        onUserClick = {},
        onSearchQueryChanged = {},
        onRefresh = {},
        onFilterAll = {},
        onFilterBanned = {},
        onFilterPro = {},
        onFilterAdmin = {},
        onBanUser = {},
        onUnbanUser = {},
        onConfirmBan = {},
        onDismissBanDialog = {},
        onShowProDialog = {},
        onConfirmPro = {},
        onDismissProDialog = {},
        onShowAdminDialog = {},
        onConfirmAdmin = {},
        onDismissAdminDialog = {},
    )
}

private val manageQuestionsUi: @Composable () -> Unit = {
    ManageQuestionsContent(
        state = ManageQuestionsState(
            filteredQuestions = listOf(previewQuestionDomain),
            isLoading = false,
        ),
        onNavigateUp = {},
        onQuestionClick = {},
        onSearchQueryChanged = {},
        onFilterAll = {},
        onFilterHidden = {},
        onRefresh = {},
        onHideQuestion = {},
        onUnhideQuestion = {},
        onConfirmHide = {},
        onDismissHideDialog = {},
    )
}

private val manageAnswersUi: @Composable () -> Unit = {
    ManageAnswersContent(
        state = ManageAnswersState(
            filteredAnswers = listOf(previewAnswerDomain),
            isLoading = false,
        ),
        onNavigateUp = {},
        onAnswerClick = {},
        onSearchQueryChanged = {},
        onRefresh = {},
        onDeleteAnswer = {},
        onConfirmDelete = {},
        onDismissDeleteDialog = {},
    )
}

private val manageAnnouncementsUi: @Composable () -> Unit = {
    ManageAnnouncementsContent(
        state = ManageAnnouncementsState(
            announcements = listOf(previewNotification),
            isLoading = false,
        ),
        onNavigateUp = {},
        onTitleChanged = {},
        onMessageChanged = {},
        onCreateAnnouncement = {},
        onShowCreateDialog = {},
        onDismissCreateDialog = {},
        onDeleteAnnouncement = {},
        onConfirmDelete = {},
        onDismissDeleteDialog = {},
        onRefresh = {},
        snackbarHostState = SnackbarHostState(),
    )
}

private val manageJobsUi: @Composable () -> Unit = {
    ManageJobsContent(
        state = ManageJobsState(
            jobs = listOf(previewJob),
            companyLogos = emptyMap(),
            isLoading = false,
        ),
        onNavigateUp = {},
        onCompanyClick = {},
        onJobClick = {},
        onApproveJob = {},
        onRejectJob = {},
        onConfirmApprove = {},
        onDismissDialog = {},
        onRefresh = {},
        snackbarHostState = SnackbarHostState(),
    )
}

private val manageCompaniesUi: @Composable () -> Unit = {
    ManageCompaniesContent(
        state = ManageCompaniesState(
            companies = listOf(previewCompany),
            filteredCompanies = listOf(previewCompany),
            isLoading = false,
        ),
        onNavigateUp = {},
        onCompanyClick = {},
        onSearchQueryChanged = {},
        onToggleSubscription = { _, _ -> },
        onRefresh = {},
        snackbarHostState = SnackbarHostState(),
    )
}

private val manageReportsUi: @Composable () -> Unit = {
    ManageReportsContent(
        state = ManageReportsState(
            allReports = listOf(previewReport),
            filteredReports = listOf(previewReport),
            isLoading = false,
        ),
        onNavigateUp = {},
        onFilterSelected = {},
        onRefresh = {},
        onSelectReport = {},
        onDismissDetail = {},
        onDismissReport = {},
        onDeleteContent = {},
        onBanUser = {},
        snackbarHostState = SnackbarHostState(),
    )
}

// ── Fixtures (mirrors the private fixtures in each screen's @Preview) ────────

private val previewQuestion = QuestionFeedUiModel(
    id = 1,
    authorAccountId = 10,
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    timeAgo = "2h",
    category = "Android",
    title = "How to handle deep links in Compose Navigation?",
    preview = "I'm trying to navigate to a specific screen using a deep link but it keeps opening the Home screen.",
    codeSnippet = null,
    tags = listOf("compose", "navigation"),
    likes = 12,
    answers = 3,
    isAuthorPro = true,
)

private val previewQuestion2 = QuestionFeedUiModel(
    id = 2,
    authorAccountId = 11,
    authorName = "Sara Khaled",
    authorAvatarUrl = "",
    timeAgo = "5h",
    category = "Kotlin",
    title = "What is the difference between apply and also?",
    preview = "Both are scope functions but I can't tell when to use which.",
    codeSnippet = "val l = list.map {\n    it * 2\n}.also { println(it) }",
    tags = listOf("kotlin", "scope-functions"),
    likes = 8,
    answers = 5,
)

private val previewQuestionPinned = QuestionFeedUiModel(
    id = 3,
    authorAccountId = 10,
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    timeAgo = "1d",
    category = "Compose",
    title = "Pinned: Best practices for state hoisting",
    preview = "A pinned question to show off the pinned feed section.",
    codeSnippet = null,
    tags = listOf("compose"),
    likes = 50,
    answers = 20,
    isPinned = true,
)

private val previewQuestionDetail = QuestionDetailUiModel(
    title = "How to handle deep links in Compose Navigation?",
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    authorAccountId = 10,
    timeAgo = "2h ago",
    tags = listOf("compose", "navigation"),
    body = "I'm trying to navigate to a specific screen using a deep link but it keeps opening the Home screen.",
    language = SyntaxLanguage.KOTLIN,
    code = "composable<Route.QuestionDetails>(\n    deepLinks = listOf(navDeepLink { uriPattern = \"devz://question/{id}\" })\n)",
    likes = 12,
    answersCount = 3,
    isAuthorPro = true,
)

private val previewAnswerUi = AnswerUiModel(
    answerId = 1,
    authorName = "Sara Khaled",
    avatarUrl = "",
    authorAccountId = 11,
    body = "Make sure your deep link URI matches the pattern exactly, including the scheme.",
    isAccepted = true,
    likes = 5,
    timeAgo = "1h ago",
    code = "android:host=\"question\"",
    language = SyntaxLanguage.KOTLIN,
)

private val sampleProfile = ProfileUiModel(
    fullName = "Ahmad Khatib",
    username = "akhatib_dev",
    imageUrl = "",
    bio = "Android developer building with Kotlin & Compose. Open source enthusiast.",
    points = "1280",
    answerCount = 42,
    questionCount = 17,
    acceptedRate = "72%",
    skills = listOf("Kotlin", "Jetpack Compose", "Supabase"),
    githubUrl = "github.com/akhatib",
    linkedInUrl = "",
    websiteUrl = "akhatib.dev",
    isPro = true,
)

private val sampleProfileState = ProfileState(
    id = 1,
    profile = sampleProfile,
    isOwnProfile = true,
    followersCount = 128,
    followingCount = 64,
)

private val adminProfile = sampleProfile.copy(isAdmin = true)

private val adminProfileState = sampleProfileState.copy(profile = adminProfile)

private val previewDashboardState = CompanyDashboardState(
    companyName = "Acme Labs",
    logoUrl = "",
    website = "https://acme.dev",
    description = "We build developer tools.",
    subscriptionStatus = "active",
    offeredJobs = listOf(
        CompanyJobUiModel(
            id = 1,
            title = "Senior Android Developer",
            status = "approved",
            createdAt = "2024-01-01T00:00:00Z",
        )
    ),
    reservedJobs = listOf(
        CompanyJobUiModel(
            id = 2,
            title = "Backend Engineer",
            status = "pending",
            createdAt = "2024-01-02T00:00:00Z",
        )
    ),
    selectedTab = 0,
    isLoading = false,
)

private val previewProposal = ProposalUiModel(
    applicationId = 1,
    applicantId = 2,
    fullName = "Sara Ali",
    username = "sara",
    avatarUrl = "",
    email = "sara@example.com",
    whatsapp = "+12345678",
    points = 120,
    status = "pending",
    coverLetter = "I am very interested in this role and have five years of Android experience.",
    createdAt = "2024-01-01T00:00:00Z",
    isPro = true,
)

private val previewAccount = Account(
    id = 1,
    username = "ahmed",
    fullName = "Ahmed Ali",
    email = "ahmed@devz.app",
    password = "",
    imageUrl = "",
    bio = "Android developer",
    techStack = "Kotlin, Compose",
    githubUrl = "",
    linkedInUrl = "",
    websiteUrl = "",
    points = 120,
)

private val previewQuestionDomain = Question(
    id = 1,
    title = "How to handle deep links in Compose Navigation?",
    description = "I'm trying to navigate to a specific screen using a deep link.",
    code = "",
    likesCount = 12,
    answersCount = 3,
    tags = "compose,navigation",
    langTypeId = 1,
    accountId = 1,
    createdAt = "2024-01-01T00:00:00Z",
)

private val previewAnswerDomain = Answer(
    id = 1,
    description = "Make sure your deep link URI matches the pattern exactly, including the scheme.",
    accepted = true,
    votedIds = "",
    questionId = 1,
    accountId = 2,
    createdAt = "2024-01-01T00:00:00Z",
    code = null,
)

private val previewNotification = Notification(
    id = 1,
    typeId = 6,
    userId = 0,
    actorId = 0,
    questionId = 0,
    answerId = null,
    type = "system",
    message = "devZ maintenance\nScheduled maintenance on Sunday at 02:00 AM.",
    isRead = false,
    createdAt = "2024-01-01T00:00:00Z",
    senderType = "system",
    isGlobal = true,
)

private val previewJob = JobPosting(
    id = 1,
    companyName = "Acme Labs",
    title = "Senior Android Developer",
    description = "Build great things.",
    salaryRange = "$80k - $120k",
    jobType = "full-time",
    status = "pending",
    createdAt = "2024-01-01T00:00:00Z",
    accountId = 1,
)

private val previewCompany = CompanyUiModel(
    id = 1,
    companyName = "Acme Labs",
    logoUrl = "",
    website = "https://acme.dev",
    subscriptionStatus = "pending",
    accountId = 1,
)

private val previewReport = ReportUiModel(
    report = Report(
        id = 1,
        reporterId = 1,
        reportedType = "question",
        reportedId = 1,
        reason = "Spam",
        details = "This content looks like spam.",
        status = "pending",
        createdAt = "2024-01-01T00:00:00Z",
    ),
    reporterName = "Ahmed Ali",
    reporterAvatar = "",
    targetTitle = "How to handle deep links in Compose Navigation?",
    ownerAccountId = 2,
)
