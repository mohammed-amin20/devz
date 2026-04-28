package com.mohamed.devz.feature.question.presentation.add_edit_qestion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.feature.question.presentation.add_edit_qestion.components.DefaultFieldLabel
import com.mohamed.devz.feature.question.presentation.util.IndentationFormatter
import com.mohamed.devz.feature.question.presentation.util.PythonIndentFormatter
import com.mohamed.devz.feature.question.presentation.util.SyntaxLanguage
import com.mohamed.devz.feature.question.presentation.util.tokenize
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun AddEditQuestionScreen(
    questionId: String? = null, // null = Add, non-null = Edit
    onClose: () -> Unit,
    onPublished: () -> Unit,
    //viewModel: AddEditQuestionViewModel = hiltViewModel()
) {
    // uiState by viewModel.uiState.collectAsState()
    val isEdit = questionId != null

    val tags = listOf("kotlin", "firebase" /*"firestore", "pagination"*/)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
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
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Provide as much context as possible. High-quality\ntechnical questions attract high-quality architectural solutions.",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                DefaultFieldLabel("PROBLEM STATEMENT")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
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
            }

            item {
                DefaultFieldLabel("TECHNICAL CONTEXT")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
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
            }

            item {
                DefaultFieldLabel("CODE IMPLEMENTATION")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CyanPrimary.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CodeEditorField()
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                DefaultFieldLabel("CLASSIFICATION TAGS")
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CyanPrimary.copy(0.3f))
                                .border(
                                    width = 1.dp,
                                    color = CyanPrimary.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(50)
                                )
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
            }

            item {
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
            }

            item {
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

@Composable
fun CodeEditorField(
    modifier: Modifier = Modifier,
    language: SyntaxLanguage = SyntaxLanguage.KOTLIN,
    minLines: Int = 6,
    placeholder: String = "// Paste your code here...",
) {
    var codeInput by remember {
        mutableStateOf(
            """
def fetch_paged_data(limit: int) -> list:
# Query the database
query = db.collection("items")
.order_by("timestamp")
.limit(limit)
return query.get()
    """.trimIndent()
        )
    }

    val highlightedCode = remember(codeInput, language) {
        buildAnnotatedString {
            tokenize(codeInput, language).forEach { token ->
                withStyle(SpanStyle(color = token.color)) {
                    append(token.text)
                }
            }
        }
    }
    val codeInputState = rememberTextFieldState(highlightedCode.text)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0E0E0E),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 3.dp)
            .border(0.5.dp, Color(0xFF3C494C).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = (minLines * 20).dp)
            ) {
                val lineCount = codeInput.lines().size.coerceAtLeast(minLines)
                Column(
                    modifier = Modifier
                        .background(Color(0xFF0A0A0A))
                        .padding(horizontal = 10.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    repeat(lineCount) { index ->
                        Text(
                            text = (index + 1).toString(),
                            color = Color(0xFF3C494C),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        )
                    }
                }

                BasicTextField(
                    state = codeInputState,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = (minLines * 20).dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFE5E2E1)
                    ),
                    cursorBrush = SolidColor(Color(0xFF44D8F1)),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    decorator = { innerTextField ->
                        Box {
                            if (codeInputState.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Color(0xFF3C494C),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 20.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

// ── Format code before tokenizing ────────────────────────────────────────────
private fun formatCode(code: String, language: SyntaxLanguage): String {
    if (code.isBlank()) return code
    return if (language == SyntaxLanguage.PYTHON)
        PythonIndentFormatter().format(code)
    else
        IndentationFormatter().format(code)
}

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