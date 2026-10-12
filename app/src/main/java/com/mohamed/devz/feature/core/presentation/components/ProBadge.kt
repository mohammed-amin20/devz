package com.mohamed.devz.feature.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R
import com.mohamed.devz.ui.theme.extendedColors

@Composable
fun ProBadge(
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 10.sp,
) {
    val pro = MaterialTheme.extendedColors.pro
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(pro.copy(alpha = 0.15f))
            .border(1.dp, pro.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 1.dp),
    ) {
        Text(
            text = stringResource(R.string.pro_badge),
            color = pro,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}
