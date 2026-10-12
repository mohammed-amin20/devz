package com.mohamed.devz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R

/** Latin body family — Inter (Regular / Medium / SemiBold / Bold). */
private val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

/** Latin display family — Space Grotesk (Regular / Medium / Bold). */
private val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

/** Arabic family — IBM Plex Sans Arabic (Regular / Medium / SemiBold / Bold). */
private val IbmPlexSansArabicFamily = FontFamily(
    Font(R.font.ibm_plex_sans_arabic_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_arabic_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_arabic_semibold, FontWeight.SemiBold),
    Font(R.font.ibm_plex_sans_arabic_bold, FontWeight.Bold),
)

/**
 * Builds the full Material3 type scale for the current locale.
 *
 * English uses Space Grotesk for display/title styles and Inter for body/label.
 * Arabic uses IBM Plex Sans Arabic throughout with letter-spacing set to 0,
 * because tracking breaks Arabic connected script.
 */
fun devzTypography(arabic: Boolean): Typography {
    val display = if (arabic) IbmPlexSansArabicFamily else SpaceGroteskFamily
    val body = if (arabic) IbmPlexSansArabicFamily else InterFamily
    val tracking: (Float) -> androidx.compose.ui.unit.TextUnit =
        if (arabic) { _ -> 0.sp } else { it -> it.sp }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = tracking(-0.25f),
        ),
        displayMedium = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = tracking(0f),
        ),
        displaySmall = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = tracking(0f),
        ),
        headlineLarge = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = tracking(0f),
        ),
        headlineMedium = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = tracking(0f),
        ),
        headlineSmall = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = tracking(0f),
        ),
        titleLarge = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Bold,
            fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = tracking(0f),
        ),
        titleMedium = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Medium,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = tracking(0.15f),
        ),
        titleSmall = TextStyle(
            fontFamily = display, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = tracking(0.1f),
        ),
        bodyLarge = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Normal,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = tracking(0.5f),
        ),
        bodyMedium = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Normal,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = tracking(0.25f),
        ),
        bodySmall = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Normal,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = tracking(0.4f),
        ),
        labelLarge = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = tracking(0.1f),
        ),
        labelMedium = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Medium,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = tracking(0.5f),
        ),
        labelSmall = TextStyle(
            fontFamily = body, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = tracking(0.5f),
        ),
    )
}

/** Default (English) typography, kept for backward-compatible references. */
val Typography: Typography = devzTypography(arabic = false)
