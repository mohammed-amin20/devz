package com.mohamed.devz.feature.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.splash.presentation.SplashViewModel.SplashEvent
import com.mohamed.devz.ui.components.BrandBackground
import com.mohamed.devz.ui.components.DevzWordmark
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun SplashScreen(
    navigateToOnboarding: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToBanned: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.splashEvent.collect { event ->
            when (event) {
                SplashEvent.NavigateToOnboarding -> navigateToOnboarding()
                SplashEvent.NavigateToAuth -> navigateToAuth()
                SplashEvent.NavigateToHome -> navigateToHome()
                SplashEvent.NavigateToBanned -> navigateToBanned()
            }
        }
    }

    SplashContent(
        modifier = modifier,
        onAnimationFinished = { viewModel.onAction(SplashAction.DecideWhereToGoAction) },
    )
}

@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOut),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.75f,
        animationSpec = tween(durationMillis = 500, easing = EaseOutBack),
        label = "scale"
    )

    val progressAnim = remember { Animatable(0f) }

    val pulseTransition = rememberInfiniteTransition(label = "glow")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    LaunchedEffect(Unit) {
        visible = true
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        onAnimationFinished()
    }

    BrandBackground(
        modifier = modifier,
        accentCenter = Offset(0.5f, 0.45f),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(0.85f + 0.2f * pulse)
                        .background(
                            color = MaterialTheme.colorScheme.primary
                                .copy(alpha = 0.14f * (1f - pulse)),
                            shape = CircleShape,
                        )
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.splash_logo_desc),
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            DevzWordmark(fontSize = 42.sp, letterSpacing = 3.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(2.dp)
                    .background(
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(1.dp),
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_tagline),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.splash_version),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewSplashScreen() {
    DevzTheme {
        SplashContent(
            onAnimationFinished = {},
        )
    }
}