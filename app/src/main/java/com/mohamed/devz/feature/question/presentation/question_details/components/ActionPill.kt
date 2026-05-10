package com.mohamed.devz.feature.question.presentation.question_details.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    active: Boolean
) {
    Surface(
        shape = RoundedCornerShape(9999.dp),
        color = if (active) Primary.copy(alpha = 0.1f) else SurfaceHigh,
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .border(
                1.dp,
                if (active) Primary.copy(alpha = 0.14f) else Color.Transparent,
                RoundedCornerShape(9999.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (active) Primary else Outline,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = OnSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}