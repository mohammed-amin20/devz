package com.mohamed.devz.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Syntax-highlighting palette for code surfaces. Kept separate from
 * [ExtendedColors] so the tokenizer can stay UI-framework agnostic.
 *
 * Each variant is tuned so every token stays legible on its matching
 * `codeBackground` (dark palette on dark, light palette on light).
 */
data class SyntaxPalette(
    val keyword: Color,
    val string: Color,
    val comment: Color,
    val function: Color,
    val number: Color,
    val annotation: Color,
    val type: Color,
    val plain: Color,
)

/** Dark code palette — the original devZ syntax colours. */
internal val DarkSyntaxPalette = SyntaxPalette(
    keyword = Color(0xFFFFB87B),
    string = Color(0xFFA1EFFF),
    comment = Color(0xFF869396),
    function = Color(0xFF44D8F1),
    number = Color(0xFFF19640),
    annotation = Color(0xFFFFD700),
    type = Color(0xFF82AAFF),
    plain = Color(0xFFE5E2E1),
)

/** Light code palette — darker tokens that keep AA contrast on a near-white surface. */
internal val LightSyntaxPalette = SyntaxPalette(
    keyword = Color(0xFF9A4E00),
    string = Color(0xFF0F7B6C),
    comment = Color(0xFF6E7781),
    function = Color(0xFF0A6E8A),
    number = Color(0xFF9A5B00),
    annotation = Color(0xFF8A6D00),
    type = Color(0xFF0550AE),
    plain = Color(0xFF1F2328),
)

/**
 * Semantic colours that Material3's [androidx.compose.material3.ColorScheme] does
 * not model directly (success/warning states, code surfaces, Pro gold).
 *
 * Read via `MaterialTheme`-style accessor: `DevzTheme.extendedColors.success`.
 */
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val codeBackground: Color,
    val codeBorder: Color,
    val codeText: Color,
    val pro: Color,
    val proContainer: Color,
    val codeSyntax: SyntaxPalette,
)

internal val DarkExtendedColors = ExtendedColors(
    success = Color(0xFF6BD98A),
    onSuccess = Color(0xFF00391A),
    successContainer = Color(0xFF00522A),
    onSuccessContainer = Color(0xFFB7F5C4),
    warning = Color(0xFFFFC107),
    onWarning = Color(0xFF3E2E00),
    warningContainer = Color(0xFF5C4500),
    onWarningContainer = Color(0xFFFFDF9E),
    info = Color(0xFF44D8F1),
    onInfo = Color(0xFF00363E),
    codeBackground = Color(0xFF0D1117),
    codeBorder = Color(0xFF2A3A3A),
    codeText = Color(0xFFE6EDF3),
    pro = Color(0xFFFFD700),
    proContainer = Color(0xFF3D3200),
    codeSyntax = DarkSyntaxPalette,
)

internal val LightExtendedColors = ExtendedColors(
    success = Color(0xFF1E7A3C),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFB7F5C4),
    onSuccessContainer = Color(0xFF00210E),
    warning = Color(0xFF8A6100),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFDF9E),
    onWarningContainer = Color(0xFF2A1F00),
    info = Color(0xFF00687A),
    onInfo = Color(0xFFFFFFFF),
    codeBackground = Color(0xFFF6F8FA),
    codeBorder = Color(0xFFD0D7DE),
    codeText = Color(0xFF1F2328),
    pro = Color(0xFF9A7B00),
    proContainer = Color(0xFFFFF3C4),
    codeSyntax = LightSyntaxPalette,
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }
