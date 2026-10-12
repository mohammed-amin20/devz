package com.mohamed.devz.feature.authentication.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.tooling.preview.Preview
import com.mohamed.devz.feature.authentication.presentation.components.login_screen.presentation.LoginScreen
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.SignUpScreen
import com.mohamed.devz.ui.components.BrandBackground
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onCompanyRegisterSuccess: () -> Unit = onRegisterSuccess,
    onBanned: () -> Unit

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

    LaunchedEffect(Unit) {
        visible = true
    }

    BrandBackground(
        modifier = modifier,
        accentCenter = animatedOffset,
    ) {
        AnimatedContent(
            targetState = index,
            transitionSpec = {
                (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.96f))
                    .togetherWith(fadeOut(tween(250)) + scaleOut(tween(250), targetScale = 0.98f))
            },
            label = "auth_swap",
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .scale(scale),
        ) { target ->
            when (target) {
                0 -> LoginScreen(
                    onNavigateToRegister = { index = 1 },
                    onLoginSuccess = onLoginSuccess,
                    onBanned = onBanned
                )

                1 -> SignUpScreen(
                    onNavigateToLogin = { index = 0 },
                    onRegisterSuccess = onRegisterSuccess,
                    onCompanyRegisterSuccess = onCompanyRegisterSuccess,
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PrevAuth() {
    DevzTheme {
        AuthScreen(
            onLoginSuccess = {},
            onRegisterSuccess = {},
            onBanned = {}
        )
    }
}
