package com.mohamed.devz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R

/**
 * The "dev" + "Z" wordmark. Wraps the Latin run in an LTR override so the word
 * order stays correct inside RTL/Arabic layouts.
 */
@Composable
fun DevzWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    letterSpacing: TextUnit = 0.sp,
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append("\u202A")
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            ) { append("dev") }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            ) { append("Z") }
            append("\u202C")
        },
        fontSize = fontSize,
        letterSpacing = letterSpacing,
        style = MaterialTheme.typography.titleLarge,
    )
}

/** Logo badge + [DevzWordmark], themed for the immersive entry screens. */
@Composable
fun BrandHero(
    modifier: Modifier = Modifier,
    logoSize: Dp = 48.dp,
    cornerRadius: Dp = 16.dp,
    fontSize: TextUnit = 28.sp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(logoSize)
                .clip(RoundedCornerShape(cornerRadius)),
        )
        DevzWordmark(fontSize = fontSize)
    }
}
