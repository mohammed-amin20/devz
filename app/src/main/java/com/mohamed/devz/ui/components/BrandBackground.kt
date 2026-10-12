package com.mohamed.devz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Full-screen branded backdrop shared by the immersive entry screens
 * (Splash, Onboarding, Auth, Banned).
 *
 * Draws the active scheme's [androidx.compose.material3.ColorScheme.background]
 * and overlays a soft radial glow derived from the brand
 * [androidx.compose.material3.ColorScheme.primary], so it adapts to both
 * light and dark themes while keeping the devZ cyan identity.
 */
@Composable
fun BrandBackground(
    modifier: Modifier = Modifier,
    accentCenter: Offset = Offset(0.5f, 0.45f),
    content: @Composable BoxScope.() -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.background.luminance() < 0.5f
    val glowAlpha = if (dark) 0.22f else 0.30f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.background)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            scheme.primary.copy(alpha = glowAlpha),
                            scheme.primary.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent,
                        ),
                        center = accentCenter,
                        radius = 1100f,
                    )
                )
        )
        content()
    }
}
