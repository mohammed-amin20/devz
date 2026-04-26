package com.mohamed.devz.feature.question.presentation.add_edit_qestion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.mohamed.devz.feature.question.presentation.add_edit_qestion.components.DefaultFieldLabel
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzBlack
import com.mohamed.devz.ui.theme.DevzInput
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun AddEditQuestionScreen(
    questionId: String? = null, // null = Add, non-null = Edit
    onClose: () -> Unit,
    onPublished: () -> Unit,
    //viewModel: AddEditQuestionViewModel = hiltViewModel()
) {
    // uiState by viewModel.uiState.collectAsState()
    val isEdit = questionId != null

    val tags = listOf("kotlin", "firebase", /*"firestore", "pagination"*/)
    var showTagInput by remember { mutableStateOf(false) }

    LaunchedEffect(questionId) {
        questionId?.let { /*viewModel.onAction(AddEditAction.LoadQuestion(it))*/ }
    }

    Scaffold(
        containerColor = Color(0xFF131313),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
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
                    onClick = { /*viewModel.onAction(AddEditAction.Publish, onPublished)*/ },
                    shape = RoundedCornerShape(10.dp),
                    enabled = /*!uiState.isLoading*/ true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    modifier = Modifier
                        .shadow(
                            spotColor = CyanPrimary,
                            elevation = 12.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text("Publish", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {

            // ── Hero title ────────────────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )) {
                        append("Architect Your")
                    }
                    withStyle(style = SpanStyle(
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        shadow = Shadow(
                            color = CyanPrimary,
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    )) {
                        append("\nInquiry")
                    }
                },
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Provide as much context as possible. High-quality\ntechnical questions attract high-quality architectural solutions.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Problem Statement ─────────────────────────────────────────
            DefaultFieldLabel("PROBLEM STATEMENT")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = /*uiState.title*/ "",
                onValueChange = { /*viewModel.onAction(AddEditAction.TitleChanged(it))*/ },
                modifier = Modifier.fillMaxWidth(),
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

            // ── Technical Context ─────────────────────────────────────────
            DefaultFieldLabel("TECHNICAL CONTEXT")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = /*uiState.body*/ "",
                onValueChange = { /*viewModel.onAction(AddEditAction.BodyChanged(it))*/ },
                modifier = Modifier
                    .fillMaxWidth()
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

            // ── Code Implementation ───────────────────────────────────────
            DefaultFieldLabel("CODE IMPLEMENTATION")
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0A1515),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    // Cyan left border
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(160.dp)
                            .background(CyanPrimary.copy(alpha = 0.4f))
                    )
                    OutlinedTextField(
                        value = /*uiState.codeSnippet*/ "",
                        onValueChange = { /*viewModel.onAction(AddEditAction.CodeChanged(it))*/ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = {
                            Text(
                                "// Paste your code here...",
                                color = TextSubtle,
                                fontSize = 12.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF7DCFB6),
                            unfocusedTextColor = Color(0xFF7DCFB6),
                            cursorColor = CyanPrimary,
                            focusedContainerColor = Color(0xFF0E0E0E),
                            unfocusedContainerColor = Color(0xFF0E0E0E)
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Classification Tags ───────────────────────────────────────
            DefaultFieldLabel("CLASSIFICATION TAGS")
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CyanPrimary.copy(0.3f))
                            .border(width = 1.dp, color = CyanPrimary.copy(alpha = 0.35f), shape = RoundedCornerShape(50))
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

                item {
                    // Add tag button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1A2424))
                            .clickable { /*viewModel.onAction(AddEditAction.ShowTagInput)*/ showTagInput =
                                true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
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

            // Tag input field
            if (/*uiState.*/showTagInput) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = /*uiState.tagInput*/ "",
                    onValueChange = { /*viewModel.onAction(AddEditAction.TagInputChanged(it))*/ },
                    modifier = Modifier.fillMaxWidth(),
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

            // ── Review notice ─────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1C1B1B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(160.dp)
                            .background(CyanPrimary.copy(alpha = 0.4f))
                    )
                    Row(
                        modifier = Modifier.padding(16.dp),
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
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Shared helper ─────────────────────────────────────────────────────────────
@Composable
fun devzTextFieldColors() = OutlinedTextFieldDefaults.colors(
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
            onClose = {},
            onPublished = {}
        )
    }
}