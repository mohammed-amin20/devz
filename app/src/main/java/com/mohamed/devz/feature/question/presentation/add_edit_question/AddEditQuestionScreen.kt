package com.mohamed.devz.feature.question.presentation.add_edit_question

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.add_edit_question.components.CodeEditorField
import com.mohamed.devz.feature.question.presentation.add_edit_question.components.DefaultFieldLabel
import com.mohamed.devz.feature.question.presentation.add_edit_question.components.LanguageDropdownField
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.formatCode
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

@Composable
fun AddEditQuestionScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    questionId: Int? = null,
    viewModel: AddEditQuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEdit = questionId != null

    var selectedLanguage by remember { mutableStateOf(SyntaxLanguage.KOTLIN) }

    LaunchedEffect(questionId) {
        questionId?.let { viewModel.onAction(AddEditQuestionAction.LoadQuestion(it)) }
    }

    LaunchedEffect(uiState.selectedLangTypeId, uiState.languageTypes) {
        if (uiState.languageTypes.isNotEmpty()) {
            val match = uiState.languageTypes.find { it.id == uiState.selectedLangTypeId }
            selectedLanguage = match?.let { typeToSyntaxLanguage(it.type) } ?: SyntaxLanguage.KOTLIN
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.addedit_close_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        if (isEdit) R.string.addedit_title_edit else R.string.addedit_title_ask
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Button(
                onClick = {
                    viewModel.onAction(AddEditQuestionAction.Publish(navigateUp))
                },
                shape = RoundedCornerShape(10.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        stringResource(R.string.addedit_publish),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                ) {
                    append(stringResource(R.string.addedit_hero_line1))
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.primary,
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    )
                ) {
                    append("\n" + stringResource(R.string.addedit_hero_line2))
                }
            },
            lineHeight = 32.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.addedit_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        DefaultFieldLabel(
            text = stringResource(R.string.addedit_label_language),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LanguageDropdownField(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { lang ->
                selectedLanguage = lang
                val typeId = uiState.languageTypes
                    .find { it.type.equals(lang.label, ignoreCase = true) }
                    ?.id ?: 1
                viewModel.onAction(AddEditQuestionAction.LanguageSelected(typeId))
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        DefaultFieldLabel(
            text = stringResource(R.string.addedit_label_title),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.onAction(AddEditQuestionAction.TitleChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    stringResource(R.string.addedit_title_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            isError = uiState.titleError != null,
            shape = RoundedCornerShape(12.dp),
            colors = devzTextFieldColors(isError = uiState.titleError != null)
        )

        uiState.titleError?.let {
            Text(
                text = it.asString(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        DefaultFieldLabel(
            text = stringResource(R.string.addedit_label_body),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.body,
            onValueChange = { viewModel.onAction(AddEditQuestionAction.BodyChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(140.dp),
            isError = uiState.bodyError != null,
            placeholder = {
                Text(
                    stringResource(R.string.addedit_body_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = devzTextFieldColors(isError = uiState.bodyError != null)
        )

        uiState.bodyError?.let {
            Text(
                text = it.asString(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultFieldLabel(
                text = stringResource(R.string.addedit_label_code)
            )
            Text(
                text = stringResource(R.string.addedit_format_code),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        viewModel.onAction(
                            AddEditQuestionAction.CodeChanged(
                                formatCode(
                                    selectedLanguage,
                                    uiState.code
                                )
                            )
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            CodeEditorField(
                modifier = Modifier
                    .padding(start = 3.dp),
                code = uiState.code,
                onCodeChange = { viewModel.onAction(AddEditQuestionAction.CodeChanged(it)) }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        DefaultFieldLabel(
            text = stringResource(R.string.addedit_label_tags),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "#$tag",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    viewModel.onAction(
                                        AddEditQuestionAction.RemoveTag(tag)
                                    )
                                }
                        )
                    }
                }
            }
            Button(
                onClick = { viewModel.onAction(AddEditQuestionAction.ShowTagInput) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.addedit_add_tag_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.addedit_add_tag),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (uiState.showTagInput) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.tagInput,
                onValueChange = { viewModel.onAction(AddEditQuestionAction.TagInputChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.addedit_tag_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { viewModel.onAction(AddEditQuestionAction.AddTag) }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = devzTextFieldColors()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        uiState.error?.let { error ->
            Text(
                text = error.asString(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        stringResource(R.string.addedit_review_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.addedit_review_body),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun typeToSyntaxLanguage(type: String): SyntaxLanguage {
    return SyntaxLanguage.entries.firstOrNull { it.label.equals(type, ignoreCase = true) }
        ?: SyntaxLanguage.GENERIC
}

@Composable
private fun devzTextFieldColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    },
    unfocusedBorderColor = if (isError) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    },
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
)

@Preview(name = "AddEdit — Dark")
@Composable
private fun PreviewAddEditQuestionScreenDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        AddEditQuestionScreen(navigateUp = {})
    }
}

@Preview(name = "AddEdit — Light")
@Composable
private fun PreviewAddEditQuestionScreenLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        AddEditQuestionScreen(navigateUp = {})
    }
}
