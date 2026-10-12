package com.mohamed.devz.feature.onboarding.presentation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.onboarding.presentation.components.FirstScreen
import com.mohamed.devz.feature.onboarding.presentation.components.SecondScreen
import com.mohamed.devz.feature.onboarding.presentation.components.ThirdScreen
import com.mohamed.devz.ui.components.BrandBackground
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.DevzTextButton
import com.mohamed.devz.ui.theme.DevzTheme
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    OnboardingContent(
        modifier = modifier,
        onFinish = {
            viewModel.onAction(OnboardingAction.FinishAction)
            onFinish()
        },
    )
}

@Composable
fun OnboardingContent(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
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

    BrandBackground(
        modifier = modifier,
        accentCenter = Offset(0.75f, 0.6f),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .scale(scale)
        ) { page ->
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = pageOffset * 90f
                        this.alpha = 1f - 0.6f * pageOffset.absoluteValue.coerceIn(0f, 1f)
                    }
            ) {
                when (page) {
                    0 -> FirstScreen()
                    1 -> SecondScreen()
                    2 -> ThirdScreen()
                }
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
            DevzButton(
                label = stringResource(
                    if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next
                ),
                onClick = {
                    scope.launch {
                        if (!isLastPage) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinish()
                        }
                    }
                },
            )
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
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }

        if (!isLastPage) {
            DevzTextButton(
                label = stringResource(R.string.onboarding_skip),
                onClick = onFinish,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .alpha(alpha)
                    .scale(scale),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PrevS1() {
    DevzTheme {
        OnboardingContent(
            onFinish = {}
        )
    }
}