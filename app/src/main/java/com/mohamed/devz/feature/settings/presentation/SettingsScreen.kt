package com.mohamed.devz.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl),
        ) {
            SettingGroup(title = stringResource(R.string.settings_theme)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
                    modes.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.themeMode == mode,
                            onClick = { viewModel.onAction(SettingsAction.SelectThemeMode(mode)) },
                            shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                        ) {
                            Text(
                                when (mode) {
                                    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                                    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                                    ThemeMode.DARK -> stringResource(R.string.theme_dark)
                                }
                            )
                        }
                    }
                }
            }

            SettingGroup(title = stringResource(R.string.settings_language)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val languages = listOf(AppLanguage.SYSTEM, AppLanguage.ENGLISH, AppLanguage.ARABIC)
                    languages.forEachIndexed { index, language ->
                        SegmentedButton(
                            selected = uiState.language == language,
                            onClick = { viewModel.onAction(SettingsAction.SelectLanguage(language)) },
                            shape = SegmentedButtonDefaults.itemShape(index, languages.size),
                        ) {
                            Text(
                                when (language) {
                                    AppLanguage.SYSTEM -> stringResource(R.string.language_system)
                                    AppLanguage.ENGLISH -> stringResource(R.string.language_english)
                                    AppLanguage.ARABIC -> stringResource(R.string.language_arabic)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        content()
    }
}

@Preview(name = "Settings — Dark")
@Composable
private fun PreviewSettingsDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        SettingsScreen(onNavigateUp = {})
    }
}

@Preview(name = "Settings — Light", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewSettingsLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        SettingsScreen(onNavigateUp = {})
    }
}
