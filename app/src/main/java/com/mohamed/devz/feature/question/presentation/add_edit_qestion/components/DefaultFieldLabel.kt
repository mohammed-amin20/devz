package com.mohamed.devz.feature.question.presentation.add_edit_qestion.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mohamed.devz.ui.theme.LabelGray

@Composable
fun DefaultFieldLabel(text: String) {
    Text(
        text = text,
        color = LabelGray,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        style = MaterialTheme.typography.bodyMedium
    )
}