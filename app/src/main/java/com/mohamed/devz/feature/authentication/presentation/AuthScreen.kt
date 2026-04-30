package com.mohamed.devz.feature.authentication.presentation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginScreen
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpScreen

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var index by remember { mutableIntStateOf(0) }

    val targetOffset = when (index) {
        0 -> Offset(0.5f, 0.45f)
        1 -> Offset(0.65f, 0.58f)
        else -> Offset(0.5f, 0.45f)
    }
    val animatedOffset by animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic),
        label = "gradientCenterAnimation"
    )

    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseInOut),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.75f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutBack),
        label = "scale"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D3333),
                        Color(0xFF0A1A1A),
                        Color(0xFF060D0D)
                    ),
                    center = animatedOffset,
                    radius = 1200f
                )
            )
            .then(modifier)
            .alpha(alpha)
            .scale(scale)
    ) {
        when(index) {
            0 -> LoginScreen(
                onNavigateToRegister = { index = 1 },
                onLoginSuccess = onLoginSuccess
            )

            1 -> SignUpScreen(
                onNavigateToLogin = { index = 0 },
                onRegisterSuccess = { index = 0 }
            )
        }
    }
}

@Preview (showSystemUi = true)
@Composable
private fun PrevAuth() {
    AuthScreen(
        onLoginSuccess = {}
    )
}