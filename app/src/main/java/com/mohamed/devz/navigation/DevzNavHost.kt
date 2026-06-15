package com.mohamed.devz.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mohamed.devz.feature.authentication.presentation.AuthScreen
import com.mohamed.devz.feature.onboarding.presentation.OnboardingScreen
import com.mohamed.devz.feature.onboarding.presentation.OnboardingViewModel
import com.mohamed.devz.feature.profile.presentation.edit_profile.EditProfileScreen
import com.mohamed.devz.feature.question.presentation.add_edit_question.AddEditQuestionScreen
import com.mohamed.devz.feature.question.presentation.question_details.QuestionDetailScreen
import com.mohamed.devz.feature.splash.presentation.SplashScreen
import com.mohamed.devz.navigation.components.HomeScreen

@Composable
fun DevzNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var profileRefreshCounter by remember { mutableIntStateOf(0) }

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
        composable<Route.Home> {
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
                profileRefreshCounter = profileRefreshCounter,
                modifier = modifier
            )
        }
        composable<Route.QuestionDetails> {
            val id = it.toRoute<Route.QuestionDetails>().id

            QuestionDetailScreen(
                questionId = id,
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
    }
}