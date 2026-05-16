package com.mohamed.devz.feature.question.presentation.add_edit_qestion

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.feature.question.presentation.add_edit_qestion.components.CodeEditorField
import com.mohamed.devz.feature.question.presentation.add_edit_qestion.components.DefaultFieldLabel
import com.mohamed.devz.feature.question.presentation.add_edit_qestion.components.LanguageDropdownField
import com.mohamed.devz.feature.question.presentation.question_details.components.Bg
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.formatCode
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun AddEditQuestionScreen(
    questionId: String? = null, // null = Add, non-null = Edit
    navigateUp: () -> Unit,
    //viewModel: AddEditQuestionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // uiState by viewModel.uiState.collectAsState()
    val isEdit = questionId != null

    var selectedLanguage by remember { mutableStateOf(SyntaxLanguage.KOTLIN) }
    var code by remember { mutableStateOf("") }
    val tags = listOf("kotlin", "firebase" /*"firestore", "pagination"*/)
    var showTagInput by remember { mutableStateOf(false) }

    LaunchedEffect(questionId) {
        questionId?.let { /*viewModel.onAction(AddEditAction.LoadQuestion(it))*/ }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .imePadding()
            .then(modifier)
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Bg)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Filled.Close, null, tint = TextWhite.copy(alpha = 0.3f))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEdit) "Edit Question" else "Ask Question",
                        color = CyanPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Button(
                    onClick = {
                        //viewModel.onAction(AddEditAction.Publish, onPublished)
                        navigateUp()
                    },
                    shape = RoundedCornerShape(10.dp),
                    enabled = /*!uiState.isLoading*/ true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
//                    modifier = Modifier
//                        .shadow(
//                            spotColor = CyanPrimary,
//                            elevation = 12.dp,
//                            shape = RoundedCornerShape(10.dp)
//                        )
                ) {
                    Text(
                        "Publish",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    ) {
                        append("Architect Your")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            shadow = Shadow(
                                color = CyanPrimary,
                                offset = Offset(0f, 0f),
                                blurRadius = 20f
                            )
                        )
                    ) {
                        append("\nInquiry")
                    }
                },
                lineHeight = 32.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Provide as much context as possible. High-quality\ntechnical questions attract high-quality architectural solutions.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        item {
            DefaultFieldLabel(
                text = "CHOOSE LANGUAGE",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LanguageDropdownField(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            DefaultFieldLabel(
                text = "PROBLEM STATEMENT",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = /*uiState.title*/ "",
                onValueChange = { /*viewModel.onAction(AddEditAction.TitleChanged(it))*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        "e.g., How to implement firestore pagination...",
                        color = TextSubtle,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = devzTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            DefaultFieldLabel(
                text = "TECHNICAL CONTEXT",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = /*uiState.body*/ "",
                onValueChange = { /*viewModel.onAction(AddEditAction.BodyChanged(it))*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(140.dp),
                placeholder = {
                    Text(
                        "Describe what you're trying to achieve and what you've tried so far...",
                        color = TextSubtle,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = devzTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultFieldLabel(
                    text = "CODE IMPLEMENTATION"
                )
                Text(
                    text = "FORMAT CODE",
                    color = CyanPrimary,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { code = formatCode(selectedLanguage, code) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CyanPrimary.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CodeEditorField(
                    modifier = Modifier
                        .padding(start = 3.dp),
                    code = code,
                    onCodeChange = { code = it }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            DefaultFieldLabel(
                text = "CLASSIFICATION TAGS",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(CyanPrimary.copy(0.3f))
                            .border(
                                width = 1.dp,
                                color = CyanPrimary.copy(alpha = 0.35f),
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
                                color = CyanPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.Close,
                                null,
                                tint = CyanPrimary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { /*viewModel.onAction(AddEditAction.RemoveTag(tag))*/ }
                            )
                        }
                    }
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A2424)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Add,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Tag", color = TextGray, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            if (/*uiState.*/showTagInput) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = /*uiState.tagInput*/ "",
                    onValueChange = { /*viewModel.onAction(AddEditAction.TagInputChanged(it))*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "e.g. Kotlin, Firebase",
                            color = TextSubtle,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { /*viewModel.onAction(AddEditAction.AddTag)*/ }) {
                            Icon(Icons.Filled.Check, null, tint = CyanPrimary)
                        }
                    },
                    colors = devzTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyanPrimary.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C1B1B))
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Review before publishing",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ensure all sensitive API keys and credentials have been removed from your code snippets.",
                            color = TextGray,
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
}

@Composable
private fun devzTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanPrimary,
    focusedContainerColor = Color(0xFF2A2A2A),
    unfocusedContainerColor = Color(0xFF2A2A2A)
)

@Preview
@Composable
private fun PreviewAddEditQuestionScreen() {
    DevzTheme {
        AddEditQuestionScreen(
            navigateUp = {}
        )
    }
}