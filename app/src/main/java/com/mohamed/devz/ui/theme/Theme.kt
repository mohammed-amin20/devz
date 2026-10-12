package com.mohamed.devz.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Root theme for devZ.
 *
 * - Dynamic colour is disabled so the brand cyan is always used.
 * - [ThemeMode] resolves the light/dark scheme; [isArabicOverride] forces the
 *   Arabic type family (used by previews).
 * - Edge-to-edge: the system bars stay transparent and their icon appearance
 *   is switched to match the active scheme.
 */
@Composable
fun DevzTheme(
    themeMode: ThemeMode = ThemeMode.default,
    isArabicOverride: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val isArabic = isArabicOverride
        ?: (LocalConfiguration.current.locales[0].language == "ar")

    val colorScheme = if (darkTheme) DevzDarkColorScheme else DevzLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = Color.Transparent.toArgb()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalSpacing provides Spacing(),
        LocalElevation provides Elevation(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = devzTypography(arabic = isArabic),
            shapes = DevzShapes,
            content = content,
        )
    }
}

/** Semantic colours not covered by Material3's [androidx.compose.material3.ColorScheme]. */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

/** Central spacing scale. */
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

/** Central elevation scale. */
val MaterialTheme.elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
