package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mohamed.devz.feature.core.presentation.components.DevzBrandHeader
import com.mohamed.devz.feature.report.presentation.ReportMenu
import com.mohamed.devz.ui.theme.spacing

@Composable
fun TopBar(
    onReport: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.lg,
                vertical = MaterialTheme.spacing.md,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DevzBrandHeader()
        if (onReport != null) {
            ReportMenu(onReport = onReport)
        }
    }
}
