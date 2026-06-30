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
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileScreen
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionScreen
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailScreen
import com.mohamed.devz.feature.splash.presentation.SplashScreen
import com.mohamed.devz.navigation.components.home.HomeScreen
import com.mohamed.devz.navigation.components.home.HomeViewModel

@Composable
fun DevzNavHost(
    modifier: Modifier = Modifier,
    pendingQuestionId: Int? = null
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
                modifier = modifier
            )
        }
        composable<Route.Home> { backStackEntry ->
            val switchToProfileTab by backStackEntry
                .savedStateHandle
                .getStateFlow("switchToProfileTab", false)
                .collectAsState()

            LaunchedEffect(pendingQuestionId) {
                if (!handledDeepLink && pendingQuestionId != null) {
                    handledDeepLink = true
                    navController.navigate(Route.QuestionDetails(pendingQuestionId))
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
                onLogout = {
                    navController.apply {
                        popBackStack()
                        navigate(Route.Auth)
                    }
                },
                switchToProfileTab = switchToProfileTab,
                profileRefreshCounter = profileRefreshCounter,
                modifier = modifier
            )
            if (switchToProfileTab) {
                backStackEntry.savedStateHandle.remove<Boolean>("switchToProfileTab")
            }
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
            ProfileScreen(
                onEditProfile = {},
                onQuestionClick = { id -> navController.navigate(Route.QuestionDetails(id)) },
                onAnswerClick = { id -> navController.navigate(Route.QuestionDetails(id)) },
                onLogout = {},
                navigateUp = { navController.navigateUp() },
                modifier = modifier
            )
        }
    }
}