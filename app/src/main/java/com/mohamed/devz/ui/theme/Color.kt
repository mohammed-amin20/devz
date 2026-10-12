package com.mohamed.devz.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Brand primitives — cyan-forward identity used to build both Material3 schemes.
// ─────────────────────────────────────────────────────────────────────────────
private val Cyan050 = Color(0xFFA1EFFF)
private val Cyan200 = Color(0xFF44D8F1)
private val Cyan600 = Color(0xFF00687A)
private val Amber400 = Color(0xFFF19640)
private val Amber700 = Color(0xFF845400)
private val Gold500 = Color(0xFFFFD700)

/**
 * Dark theme — preserves the original devZ cyan-on-charcoal look as the default.
 * All foreground/background pairs target WCAG AA (>= 4.5:1 for body text).
 */
internal val DevzDarkColorScheme = darkColorScheme(
    primary = Cyan200,
    onPrimary = Color(0xFF00363E),
    primaryContainer = Color(0xFF00505C),
    onPrimaryContainer = Cyan050,
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1B2426),
    secondaryContainer = Color(0xFF32494D),
    onSecondaryContainer = Color(0xFFCCE8EB),
    tertiary = Amber400,
    onTertiary = Color(0xFF472A00),
    tertiaryContainer = Color(0xFF653E00),
    onTertiaryContainer = Color(0xFFFFDCC2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF131313),
    onBackground = Color(0xFFE5E2E1),
    surface = Color(0xFF131313),
    onSurface = Color(0xFFE5E2E1),
    surfaceVariant = Color(0xFF353534),
    onSurfaceVariant = Color(0xFFBBC9CC),
    surfaceTint = Cyan200,
    inverseSurface = Color(0xFFE5E2E1),
    inverseOnSurface = Color(0xFF131313),
    inversePrimary = Cyan600,
    outline = Color(0xFF869396),
    outlineVariant = Color(0xFF3C494C),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF393939),
    surfaceDim = Color(0xFF131313),
    surfaceContainerLowest = Color(0xFF0E0E0E),
    surfaceContainerLow = Color(0xFF1C1B1B),
    surfaceContainer = Color(0xFF1A2424),
    surfaceContainerHigh = Color(0xFF2A2A2A),
    surfaceContainerHighest = Color(0xFF353534),
)

/**
 * Light theme — clean, near-white surfaces with a deep-cyan primary so the brand
 * stays recognisable while every pair stays AA-compliant.
 */
internal val DevzLightColorScheme = lightColorScheme(
    primary = Cyan600,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Cyan050,
    onPrimaryContainer = Color(0xFF001F26),
    secondary = Color(0xFF4A6366),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8EB),
    onSecondaryContainer = Color(0xFF051F22),
    tertiary = Amber700,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCC2),
    onTertiaryContainer = Color(0xFF2A1800),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FAFA),
    onBackground = Color(0xFF191C1D),
    surface = Color(0xFFF7FAFA),
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFDBE4E6),
    onSurfaceVariant = Color(0xFF3F484A),
    surfaceTint = Cyan600,
    inverseSurface = Color(0xFF2D3132),
    inverseOnSurface = Color(0xFFEFF1F1),
    inversePrimary = Cyan200,
    outline = Color(0xFF6F797A),
    outlineVariant = Color(0xFFBFC8CA),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF7FAFA),
    surfaceDim = Color(0xFFD7DBDC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F4F5),
    surfaceContainer = Color(0xFFEBEEEF),
    surfaceContainerHigh = Color(0xFFE5E9EA),
    surfaceContainerHighest = Color(0xFFE0E3E4),
)

/** Brand gold, used by Pro affordances. */
val DevzGold: Color = Gold500

// ─────────────────────────────────────────────────────────────────────────────
// Legacy palette — kept so not-yet-migrated feature screens keep compiling.
// New code must use MaterialTheme.colorScheme / LocalExtendedColors instead.
// ─────────────────────────────────────────────────────────────────────────────

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val BackgroundDark = Color(0xFF0D1F1F)
val BackgroundCard = Color(0xFF1E2A2A)
val CyanPrimary = Color(0xFF00BCD4)
val TextWhite = Color(0xFFEEEEEE)
val TextSubtle = Color(0xFF7A9A9A)
val DotInactive = Color(0xFF3A3A3A)
val DevzBlack = Color(0xFF1C1B1B)

val DevzCard = Color(0xFF1A2424)
val DevzInput = Color(0xFF0F1A1A)
val TextGray = Color(0xFFB0B0B0)
val LabelGray = Color(0xFF8A9A9A)

val QBg = Color(0xFF131313)
val QSurfaceLow = Color(0xFF1C1B1B)
val QSurfaceHigh = Color(0xFF2A2A2A)
val QSurfaceHighest = Color(0xFF353534)
val QSurfaceLowest = Color(0xFF0E0E0E)
val QSurfaceVariant = Color(0xFF353534)
val QOutline = Color(0xFF869396)
val QOutlineVariant = Color(0xFF3C494C)
val QOnSurface = Color(0xFFE5E2E1)
val QOnSurfaceVariant = Color(0xFFBBC9CC)
val QPrimary = Color(0xFF44D8F1)
val QPrimaryContainer = Color(0xFF00BCD4)
val QPrimaryFixed = Color(0xFFA1EFFF)
val QError = Color(0xFFFFB4AB)
val QTertiary = Color(0xFFF19640)
val QTertiaryFixed = Color(0xFFFFDCC2)
