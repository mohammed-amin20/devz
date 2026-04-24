package com.mohamed.devz.feature.splash.presentation


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import kotlinx.coroutines.delay
import com.mohamed.devz.R

@Composable
fun SplashScreen(
   onNavigate: () -> Unit,
   modifier: Modifier = Modifier
) {
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

    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        visible = true
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        delay(1000)
        onNavigate()
    }

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
                    center = Offset(0.5f, 0.45f),
                    radius = 1200f
                )
            )
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "DevZ Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextWhite, fontWeight = FontWeight.Bold)) {
                        append("dev")
                    }
                    withStyle(SpanStyle(color = CyanPrimary, fontWeight = FontWeight.Bold)) {
                        append("Z")
                    }
                },
                fontSize = 42.sp,
                letterSpacing = 3.sp,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(1.dp)
                    .background(TextSubtle.copy(alpha = 0.4f))
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
                    .background(TextSubtle.copy(alpha = 0.2f), RoundedCornerShape(1.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .background(CyanPrimary, RoundedCornerShape(1.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ARCHITECTING SOLUTIONS",
                color = TextSubtle,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "v1.0.0",
                color = TextSubtle.copy(alpha = 0.6f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    SplashScreen(
        onNavigate = {}
    )
}