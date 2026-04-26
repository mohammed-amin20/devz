package com.mohamed.devz.feature.question.presentation.question_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamed.devz.feature.question.presentation.view_questions.QuestionUiModel
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzBlack
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzInput
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun QuestionDetailScreen(
    questionId: String,
    onBack: () -> Unit,
//    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
//    val uiState by viewModel.uiState.collectAsState()
    var answerText by remember { mutableStateOf("") }

    val question = QuestionUiModel(
        id = "1",
        authorName = "Amina",
        timeAgo = 2,
        isBookmarked = true,
        category = "Kotlin",
        title = "How do I make a sealed class for UI states?",
        preview = "I am trying to model loading, success, and error states in my app...",
        codeSnippet = null,
        tags = listOf("kotlin", "sealed-class", "ui-state"),
        likes = 14,
        answers = 3
    )
    val answers = listOf(
        AnswerUiModel(
            id = "a1",
            authorName = "Lina",
            role = "Member",
            isAccepted = true,
            body = "You can achieve this by defining a custom Typography and applying it in your theme.",
            likes = 12
        ),
        AnswerUiModel(
            id = "a2",
            authorName = "Yousef",
            role = "Moderator",
            isAccepted = false,
            body = "Try using a Shadow in TextStyle to create a glow effect in Compose.",
            likes = 8
        ),
        AnswerUiModel(
            id = "a3",
            authorName = "Hassan",
            role = "Expert",
            isAccepted = false,
            body = "Layering multiple Text composables can produce a stronger glow effect.",
            likes = 5
        )
    )

    LaunchedEffect(questionId) {
//        viewModel.onAction(QuestionDetailAction.LoadQuestion(questionId))
    }

    Scaffold(
        containerColor = DevzBlack,
        // ── Bottom answer input ───────────────────────────────────────────
        bottomBar = {
            Surface(color = DevzCard, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Write your answer...",
                                color = TextSubtle,
                                fontSize = 13.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFF2A3A3A),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = CyanPrimary,
                            focusedContainerColor = DevzInput,
                            unfocusedContainerColor = DevzInput
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Emoji / code icons
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.EmojiEmotions,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.Code,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Send button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            if (answerText.isNotBlank()) {
//                                viewModel.onAction(QuestionDetailAction.SubmitAnswer(answerText))
                                answerText = ""
                            }
                        }) {
                            Icon(
                                Icons.Filled.Send,
                                null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite)
                    }
                    Text("Questions", color = TextGray, fontSize = 13.sp)
                    Icon(
                        Icons.Filled.ChevronRight,
                        null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "How to implement a clean...",
                        color = TextWhite,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Question title ────────────────────────────────────────────
            item {
                Text(
                    text = /*uiState.question?.title ?: */"",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Author + tags ─────────────────────────────────────────────
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A3A3A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(/*uiState.question?.authorName ?: */"",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Asked ${/*uiState.question?.timeAgo ?:*/ "2"} hours ago",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    /*uiState.*/question?.tags?.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1A2A2A))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            tag.uppercase(),
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── Question body ─────────────────────────────────────────────
            item {
                Text(
                    text = /*uiState.question?.body ?: */"",
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Code snippet ──────────────────────────────────────────────
            /*uiState.*/question?.codeSnippet?.let { code ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0A1515)
                ) {
                    Column {
                        // Code header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2A3A3A))
                                    )
                                }
                            }
                            Text(
                                "COPY",
                                color = CyanPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        // Code
                        Text(
                            text = code,
                            color = Color(0xFF7DCFB6),
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

            // ── Like / Comment / Share ────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A2424))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.ThumbUp,
                            null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(/*uiState.*/question?.likes.toString(),
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A2424))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.ChatBubbleOutline,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(/*uiState.*/question?.answers?.toString() ?: "0",
                            color = TextWhite,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A2424))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE", color = TextGray, fontSize = 11.sp, letterSpacing = 0.5.sp)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // ── Answers header ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${question.answers} Answers",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sort by: Trending", color = TextGray, fontSize = 13.sp)
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Answers list ──────────────────────────────────────────────
            items(/*uiState.*/answers, key = { it.id }) { answer ->
                AnswerCard(answer = answer)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Answer Card ───────────────────────────────────────────────────────────────
@Composable
fun AnswerCard(answer: AnswerUiModel) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        answer.authorName,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        answer.role,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (answer.isAccepted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00BCD420))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "ACCEPTED",
                            color = CyanPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(answer.body, color = TextGray, fontSize = 14.sp, lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ThumbUp, null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(answer.likes.toString(), color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Icon(Icons.AutoMirrored.Filled.Reply, null, tint = TextGray, modifier = Modifier.size(14.dp))
            }
        }
    }
}

data class AnswerUiModel(
    val id: String,
    val authorName: String,
    val role: String,
    val isAccepted: Boolean,
    val body: String,
    val likes: Int,
)

@Preview
@Composable
private fun PreviewQuestionDetailsScreen() {
    DevzTheme {
        QuestionDetailScreen(
            questionId = "1",
            onBack = {}
        )
    }
}