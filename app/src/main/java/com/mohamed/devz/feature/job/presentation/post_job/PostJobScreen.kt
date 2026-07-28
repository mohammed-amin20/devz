package com.mohamed.devz.feature.job.presentation.post_job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

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
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = { Text("Post New Job", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg),
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

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onAction(PostJobAction.TitleChanged(it)) },
                label = { Text("Job Title") },
                placeholder = { Text("Senior Android Developer", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onAction(PostJobAction.DescriptionChanged(it)) },
                label = { Text("Description") },
                placeholder = { Text("Describe the role, responsibilities...", color = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.salaryRange,
                onValueChange = { viewModel.onAction(PostJobAction.SalaryRangeChanged(it)) },
                label = { Text("Salary Range") },
                placeholder = { Text("$80k - $120k", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = uiState.jobType.replace("-", " ").replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Job Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    JOB_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.replace("-", " ").replaceFirstChar { it.uppercase() }) },
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
                Text(text = uiState.error!!.asString(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onAction(PostJobAction.Submit(onPostSuccess)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF00363E),
                ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF00363E), modifier = Modifier.width(22.dp).height(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Submit for Approval", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanPrimary,
    unfocusedBorderColor = Color(0xFF2A3A3A),
    focusedContainerColor = DevzCard,
    unfocusedContainerColor = DevzCard,
    cursorColor = CyanPrimary,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedLabelColor = CyanPrimary,
    unfocusedLabelColor = TextGray,
)
