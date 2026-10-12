package com.mohamed.devz.feature.onboarding.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mohamed.devz.R
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun ThirdScreen() {
    OnboardingPage(
        imageRes = R.drawable.onboarding3,
        titleIntro = stringResource(R.string.onboarding_reputation_intro),
        titleAccent = stringResource(R.string.onboarding_reputation_accent),
        body = stringResource(R.string.onboarding_reputation_body),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewThirdScreen() {
    DevzTheme {
        ThirdScreen()
    }
}
