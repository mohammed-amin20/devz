package com.mohamed.devz.feature.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.onboarding.presentation.components.FirstScreen
import com.mohamed.devz.feature.onboarding.presentation.components.SecondScreen
import com.mohamed.devz.feature.onboarding.presentation.components.ThirdScreen
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzBlack
import com.mohamed.devz.ui.theme.DotInactive
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    // viewModel: OnboardingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == 2

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
                    center = Offset(0.75f, 0.6f),
                    radius = 1200f
                )
            )
            .then(modifier)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .scale(scale)
        ) { page ->
            when (page) {
                0 -> FirstScreen()
                1 -> SecondScreen()
                2 -> ThirdScreen()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 32.dp, bottom = 52.dp)
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    scope.launch {
                        if (!isLastPage) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            //viewModel.completeOnboarding()
                            onFinish()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = index == pagerState.currentPage
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) CyanPrimary else DotInactive)
                    )
                }
            }
        }

        if (!isLastPage) {
            TextButton(
                onClick = {
                    //viewModel.completeOnboarding()
                    onFinish()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .alpha(alpha)
                    .scale(scale),
                colors = ButtonDefaults.textButtonColors(contentColor = CyanPrimary)
            ) {
                Text(
                    text = "skip",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PrevS1() {
    OnboardingScreen(
        onFinish = {}
    )
}