package com.mohamed.devz.feature.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun DevzBrandHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                ) { append("dev") }
                withStyle(
                    SpanStyle(
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                ) { append("Z") }
            },
            style = MaterialTheme.typography.titleLarge
        )
    }
}
