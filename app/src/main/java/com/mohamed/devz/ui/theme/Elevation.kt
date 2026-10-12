package com.mohamed.devz.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Central elevation scale for cards, sheets, dialogs and menus.
 * Read inside composables via [LocalElevation].
 */
data class Elevation(
    val none: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
)

val LocalElevation = staticCompositionLocalOf { Elevation() }
