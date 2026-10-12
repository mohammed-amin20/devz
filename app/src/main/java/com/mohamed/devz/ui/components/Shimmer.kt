package com.mohamed.devz.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Animated shimmer (skeleton) background. Apply to placeholder Boxes at load time. */
fun Modifier.shimmer(shape: Shape): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )
    val base = MaterialTheme.colorScheme.surfaceContainerHighest
    val highlight = MaterialTheme.colorScheme.surfaceContainer
    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(progress * 900f, 0f),
        end = Offset((progress + 1f) * 900f, 300f),
    )
    background(brush = brush, shape = shape)
}

/** A shimmering placeholder block used while content is loading. */
@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    contentDescription: String? = null,
) {
    val semanticModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
        }
    } else {
        Modifier
    }
    Box(modifier = modifier.then(semanticModifier).shimmer(shape))
}