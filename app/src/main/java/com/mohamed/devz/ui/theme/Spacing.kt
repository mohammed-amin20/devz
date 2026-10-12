package com.mohamed.devz.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Central spacing scale. Read inside composables via [LocalSpacing]
 * (or `DevzTheme.spacing`), e.g. `Modifier.padding(DevzTheme.spacing.lg)`.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val screenHorizontal: Dp = 16.dp,
    val screenVertical: Dp = 16.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
