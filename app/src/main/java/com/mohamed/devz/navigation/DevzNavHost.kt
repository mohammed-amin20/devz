package com.mohamed.devz.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mohamed.devz.feature.authentication.presentation.AuthScreen
import com.mohamed.devz.feature.onboarding.presentation.OnboardingScreen
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileScreen
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionScreen
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailScreen
import com.mohamed.devz.feature.splash.presentation.SplashScreen
import com.mohamed.devz.feature.admin.presentation.admin_dashboard.AdminDashboardScreen
import com.mohamed.devz.feature.admin.presentation.manage_users.ManageUsersScreen
import com.mohamed.devz.feature.admin.presentation.manage_questions.ManageQuestionsScreen
import com.mohamed.devz.feature.admin.presentation.manage_answers.ManageAnswersScreen
import com.mohamed.devz.feature.admin.presentation.manage_announcements.ManageAnnouncementsScreen
import com.mohamed.devz.feature.authentication.presentation.BannedScreen
import com.mohamed.devz.feature.job.presentation.jobs_screen.JobsScreen
import com.mohamed.devz.feature.job.presentation.job_detail.JobDetailScreen
import com.mohamed.devz.feature.job.presentation.post_job.PostJobScreen
import com.mohamed.devz.feature.company.presentation.PendingApprovalScreen
import com.mohamed.devz.feature.company.presentation.company_job_detail.CompanyJobDetailScreen
import com.mohamed.devz.feature.company.presentation.company_profile.ProfileHostScreen
import com.mohamed.devz.feature.admin.presentation.manage_jobs.ManageJobsScreen
import com.mohamed.devz.feature.admin.presentation.manage_companies.ManageCompaniesScreen
import com.mohamed.devz.feature.admin.presentation.manage_reports.ManageReportsScreen
import com.mohamed.devz.navigation.components.home.HomeScreen
import com.mohamed.devz.navigation.components.home.HomeViewModel

@Composable
fun DevzNavHost(
    modifier: Modifier = Modifier,
    pendingQuestionId: Int? = null,
    pendingActorId: Int? = null,
    pendingJobId: Int? = null,
    pendingReportId: Int? = null,
) {
    val navController = rememberNavController()
    var profileRefreshCounter by remember { mutableIntStateOf(0) }
    var handledDeepLink by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable<Route.Splash> {
            SplashScreen(
                navigateToOnboarding = {
                    navController.apply { popBackStack(); navigate(Route.Onboarding) }
                },
                navigateToAuth = {
                    navController.apply { popBackStack(); navigate(Route.Auth) }
                },
                navigateToHome = {
                    navController.apply { popBackStack(); navigate(Route.Home) }
                },
                navigateToBanned = {
                    navController.apply { popBackStack(); navigate(Route.Banned) }
                },
                modifier = modifier
            )
        }
        composable<Route.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Auth)
                    }
                },
                modifier = modifier
            )
        }
        composable<Route.Auth> {
            AuthScreen(
                onLoginSuccess = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Home)
                    }
                },
                onRegisterSuccess = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Home)
                    }
                },
                onCompanyRegisterSuccess = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.PendingApproval)
                    }
                },
                onBanned = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Banned)
                    }
                },
                modifier = modifier
            )
        }
        composable<Route.Home> { backStackEntry ->
            val switchToProfileTab by backStackEntry
                .savedStateHandle
                .getStateFlow("switchToProfileTab", false)
                .collectAsState()

            LaunchedEffect(pendingQuestionId, pendingActorId, pendingJobId, pendingReportId) {
                if (!handledDeepLink) {
                    when {
                        pendingReportId != null -> {
                            handledDeepLink = true
                            navController.navigate(Route.ManageReports)
                        }
                        pendingActorId != null -> {
                            handledDeepLink = true
                            navController.navigate(Route.Profile(pendingActorId))
                        }
                        pendingQuestionId != null -> {
                            handledDeepLink = true
                            navController.navigate(Route.QuestionDetails(pendingQuestionId))
                        }
                        pendingJobId != null -> {
                            handledDeepLink = true
                            navController.navigate(Route.JobDetail(pendingJobId))
                        }
                    }
                }
            }

            HomeScreen(
                navigateToQuestionDetails = { id ->
                    navController.navigate(Route.QuestionDetails(id))
                },
                navigateToAddEditQuestion = { id ->
                    navController.navigate(Route.AddEditQuestion(id))
                },
                navigateToEditProfile = {
                    navController.navigate(Route.EditProfile)
                },
                navigateToProfile = { accountId ->
                    navController.navigate(Route.Profile(accountId))
                },
                navigateToJobDetail = { jobId ->
                    navController.navigate(Route.JobDetail(jobId))
                },
                navigateToCompanyJobDetail = { jobId ->
                    navController.navigate(Route.CompanyJobDetail(jobId))
                },
                navigateToPostJob = {
                    navController.navigate(Route.PostJob)
                },
                onLogout = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Auth)
                    }
                },
                switchToProfileTab = switchToProfileTab,
                profileRefreshCounter = profileRefreshCounter,
                navigateToAdminDashboard = {
                    navController.navigate(Route.AdminDashboard)
                },
                modifier = modifier
            )
            if (switchToProfileTab) {
                backStackEntry.savedStateHandle.remove<Boolean>("switchToProfileTab")
            }
        }
        composable<Route.Jobs> {
            JobsScreen(
                onJobClick = { jobId ->
                    navController.navigate(Route.JobDetail(jobId))
                },
                modifier = modifier
            )
        }
        composable<Route.JobDetail> {
            val id = it.toRoute<Route.JobDetail>().id
            JobDetailScreen(
                jobId = id,
                navigateUp = { navController.navigateUp() },
                onProfileClick = { accountId ->
                    navController.navigate(Route.Profile(accountId))
                },
                modifier = modifier
            )
        }
        composable<Route.QuestionDetails> {
            val id = it.toRoute<Route.QuestionDetails>().id
            val homeEntry = remember(it) {
                navController.getBackStackEntry(Route.Home)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(homeEntry)
            val currentAccountId by homeViewModel.currentAccountId.collectAsState()

            QuestionDetailScreen(
                questionId = id,
                navigateUp = { navController.navigateUp() },
                onEditQuestion = { questionId ->
                    navController.navigate(Route.AddEditQuestion(questionId))
                },
                onNavigateToProfile = { targetId ->
                    if (targetId == currentAccountId && currentAccountId != 0) {
                        homeEntry.savedStateHandle["switchToProfileTab"] = true
                        navController.popBackStack(route = Route.Home, inclusive = false)
                    } else {
                        navController.navigate(Route.Profile(targetId))
                    }
                },
                modifier = modifier
            )
        }
        composable<Route.AddEditQuestion> {
            val id = it.toRoute<Route.AddEditQuestion>().id

            AddEditQuestionScreen(
                questionId = id,
                navigateUp = { navController.navigateUp() },
                modifier = modifier
            )
        }
        composable<Route.EditProfile> {
            EditProfileScreen(
                navigateUp = {
                    profileRefreshCounter++
                    navController.navigateUp()
                },
                modifier = modifier
            )
        }
        composable<Route.Profile> {
            val accountId = it.toRoute<Route.Profile>().accountId
            ProfileHostScreen(
                navAccountId = accountId,
                onEditProfile = {},
                onQuestionClick = { id -> navController.navigate(Route.QuestionDetails(id)) },
                onAnswerClick = { id -> navController.navigate(Route.QuestionDetails(id)) },
                onLogout = {},
                onProfileClick = { id -> navController.navigate(Route.Profile(id)) },
                onJobClick = { id -> navController.navigate(Route.JobDetail(id)) },
                onPostJob = { navController.navigate(Route.PostJob) },
                navigateUp = { navController.navigateUp() },
                modifier = modifier
            )
        }
        composable<Route.AdminDashboard> {
            AdminDashboardScreen(
                onNavigateToUsers = { navController.navigate(Route.ManageUsers) },
                onNavigateToQuestions = { navController.navigate(Route.ManageQuestions) },
                onNavigateToAnswers = { navController.navigate(Route.ManageAnswers) },
                onNavigateToAnnouncements = { navController.navigate(Route.ManageAnnouncements) },
                onNavigateToJobs = { navController.navigate(Route.ManageJobs) },
                onNavigateToCompanies = { navController.navigate(Route.ManageCompanies) },
                onNavigateToReports = { navController.navigate(Route.ManageReports) },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable<Route.ManageUsers> {
            ManageUsersScreen(
                onNavigateUp = { navController.navigateUp() },
                onUserClick = { navController.navigate(Route.Profile(it)) },
            )
        }
        composable<Route.ManageQuestions> {
            ManageQuestionsScreen(
                onNavigateUp = { navController.navigateUp() },
                onQuestionClick = { navController.navigate(Route.QuestionDetails(it)) },
            )
        }
        composable<Route.ManageAnswers> {
            ManageAnswersScreen(
                onNavigateUp = { navController.navigateUp() },
                onAnswerClick = { navController.navigate(Route.QuestionDetails(it)) },
            )
        }
        composable<Route.ManageAnnouncements> {
            ManageAnnouncementsScreen(
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable<Route.PendingApproval> {
            PendingApprovalScreen(
                onGoToHome = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Home)
                    }
                },
                modifier = modifier,
            )
        }
        composable<Route.PostJob> {
            PostJobScreen(
                navigateUp = { navController.navigateUp() },
                onPostSuccess = { navController.navigateUp() },
                modifier = modifier,
            )
        }
        composable<Route.ManageJobs> {
            ManageJobsScreen(
                onNavigateUp = { navController.navigateUp() },
                onCompanyClick = { accountId ->
                    navController.navigate(Route.Profile(accountId))
                },
                onJobClick = { jobId ->
                    navController.navigate(Route.JobDetail(jobId))
                },
            )
        }
        composable<Route.ManageCompanies> {
            ManageCompaniesScreen(
                onNavigateUp = { navController.navigateUp() },
                onCompanyClick = { accountId ->
                    navController.navigate(Route.Profile(accountId))
                },
            )
        }
        composable<Route.ManageReports> {
            ManageReportsScreen(
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable<Route.CompanyJobDetail> {
            val id = it.toRoute<Route.CompanyJobDetail>().id
            CompanyJobDetailScreen(
                jobId = id,
                navigateUp = { navController.navigateUp() },
                onProfileClick = { accountId ->
                    navController.navigate(Route.Profile(accountId))
                },
                modifier = modifier,
            )
        }
        composable<Route.Banned> {
            BannedScreen()
        }
    }
}