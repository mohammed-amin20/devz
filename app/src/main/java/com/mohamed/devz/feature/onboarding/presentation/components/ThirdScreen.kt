package com.mohamed.devz.feature.onboarding.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R

@Composable
fun ThirdScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.04f))

        Image(
            painter = painterResource(id = R.drawable.onboarding3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(44.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 26.sp
                    )
                ) { append("Grow your ") }
                withStyle(
                    SpanStyle(
                        color = Color(0xFF00BCD4),
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 26.sp
                    )
                ) { append("reputation") }
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Help others, earn reputation, and\nbecome a recognized expert",
            color = Color(0xFFB0B0B0),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PrevS1() {
    ThirdScreen()
}