package com.mohamed.devz.feature.job.presentation.post_job

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.job.presentation.util.onLostFocus
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

private val JOB_TYPES = listOf("full-time", "part-time", "remote", "contract")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    navigateUp: () -> Unit,
    onPostSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostJobViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.postjob_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.postjob_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            DevzTextField(
                value = uiState.title,
                onValueChange = { viewModel.onAction(PostJobAction.TitleChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(onLostFocus {
                        viewModel.onAction(PostJobAction.FieldFocusChanged(PostJobField.TITLE, false))
                    }),
                label = stringResource(R.string.postjob_field_title),
                placeholder = stringResource(R.string.postjob_title_hint),
                singleLine = true,
                isError = uiState.titleTouched && uiState.titleError != null,
                errorMessage = uiState.titleError?.takeIf { uiState.titleTouched }?.asString(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            DevzTextField(
                value = uiState.description,
                onValueChange = { viewModel.onAction(PostJobAction.DescriptionChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .then(onLostFocus {
                        viewModel.onAction(PostJobAction.FieldFocusChanged(PostJobField.DESCRIPTION, false))
                    }),
                label = stringResource(R.string.postjob_field_description),
                placeholder = stringResource(R.string.postjob_description_hint),
                singleLine = false,
                isError = uiState.descriptionTouched && uiState.descriptionError != null,
                errorMessage = uiState.descriptionError?.takeIf { uiState.descriptionTouched }?.asString(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            DevzTextField(
                value = uiState.salaryRange,
                onValueChange = { viewModel.onAction(PostJobAction.SalaryRangeChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.postjob_field_salary),
                placeholder = stringResource(R.string.postjob_salary_hint),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = jobTypeLabel(uiState.jobType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.postjob_field_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = MaterialTheme.shapes.large,
                    colors = dropdownColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    JOB_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(jobTypeLabel(type)) },
                            onClick = {
                                viewModel.onAction(PostJobAction.JobTypeSelected(type))
                                expanded = false
                            },
                        )
                    }
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.error!!.asString(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            DevzButton(
                label = stringResource(R.string.postjob_submit),
                onClick = { viewModel.onAction(PostJobAction.Submit(onPostSuccess)) },
                enabled = uiState.canSubmit,
                loading = uiState.isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun jobTypeLabel(type: String): String = stringResource(
    when (type) {
        "full-time" -> R.string.jobtype_full_time
        "part-time" -> R.string.jobtype_part_time
        "remote" -> R.string.jobtype_remote
        "contract" -> R.string.jobtype_contract
        else -> R.string.jobtype_full_time
    }
)

@Composable
private fun dropdownColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)

@Preview
@Composable
private fun PreviewPostJobDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        PostJobScreen(navigateUp = {}, onPostSuccess = {})
    }
}

@Preview
@Composable
private fun PreviewPostJobLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        PostJobScreen(navigateUp = {}, onPostSuccess = {})
    }
}
