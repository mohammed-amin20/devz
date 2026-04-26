package com.mohamed.devz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_regular)),
        fontWeight = FontWeight.Normal
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.space_grotesk_bold)),
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
        fontWeight = FontWeight.Normal
    )
)