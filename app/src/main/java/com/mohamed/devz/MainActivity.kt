package com.mohamed.devz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohamed.devz.feature.onboarding.presentation.OnboardingScreen
import com.mohamed.devz.feature.splash.presentation.SplashScreen
import com.mohamed.devz.ui.theme.DevzTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevzTheme {
                val navController = rememberNavController()

                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.Splash
                    ) {
                        composable<Route.Splash> {
                            SplashScreen(
                                onNavigate = {
                                    navController.apply {
                                        popBackStack()
                                        navigate(Route.Onboarding)
                                    }
                                },
                                modifier = Modifier
                                    .padding(innerPadding)
                            )
                        }
                        composable<Route.Onboarding> {
                            OnboardingScreen(
                                onFinish = {},
                                modifier = Modifier
                                    .padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Serializable
private sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route
}