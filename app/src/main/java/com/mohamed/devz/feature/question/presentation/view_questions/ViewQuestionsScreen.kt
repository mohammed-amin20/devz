package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzBlack
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.view_questions.components.QuestionCard
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun ViewQuestionsScreen(
    onQuestionClick: (String) -> Unit,
    onAddQuestion: () -> Unit,
    onProfileClick: () -> Unit,
    //viewModel: HomeViewModel = hiltViewModel()
) {
    //val uiState by viewModel.uiState.collectAsState()

    val questions = listOf(
        QuestionUiModel(
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
        ),
        QuestionUiModel(
            id = "2",
            authorName = "Omar",
            timeAgo = 5,
            isBookmarked = false,
            category = "Android",
            title = "Why does my RecyclerView not update?",
            preview = "The list shows old data even after I submit a new list...",
            codeSnippet = "adapter.submitList(newList)",
            tags = listOf("android", "recyclerview", "listadapter"),
            likes = 9,
            answers = 2
        ),
        QuestionUiModel(
            id = "3",
            authorName = "Sara",
            timeAgo = 8,
            isBookmarked = true,
            category = "Compose",
            title = "How can I animate visibility in Jetpack Compose?",
            preview = "I want a smooth fade in/out effect when a button is pressed...",
            codeSnippet = null,
            tags = listOf("compose", "animation", "jetpack-compose"),
            likes = 21,
            answers = 5
        )
    )

    Scaffold(
        containerColor = DevzBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddQuestion,
                containerColor = CyanPrimary,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ask", modifier = Modifier.size(28.dp))
            }
        },
        /*bottomBar = {
            DevzBottomBar(
                selected = 0,
                onHome = {},
                onAsk = onAddQuestion,
                onProfile = onProfileClick
            )
        }*/
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) { append("dev") }
                                withStyle(
                                    SpanStyle(
                                        color = CyanPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) { append("Z") }
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Icon(
                            Icons.Filled.Notifications,
                            null,
                            tint = TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A3A3A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                null,
                                tint = TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        ) {
                            append("Architecting ")
                        }
                        withStyle(
                            SpanStyle(
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
                            append("Solutions")
                        }
                        withStyle(
                            SpanStyle(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        ) {
                            append(".")
                        }
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A premium workspace for developers to exchange\nhigh-level technical logic and editorial-grade code.",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                OutlinedTextField(
                    value = /*uiState.searchQuery*/ "",
                    onValueChange = { /*viewModel.onAction(HomeAction.SearchQueryChanged(it))*/ },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Query the collective intelligence...",
                            color = TextSubtle,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextSubtle) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = Color(0xFF2A3A3A),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = CyanPrimary,
                        focusedContainerColor = DevzCard,
                        unfocusedContainerColor = DevzCard
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                val tabs = listOf("Newest", "Popular", "Trending", "Bounties")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = /*index == uiState.selectedTab*/ index == 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) CyanPrimary else Color(0xFF1A2424))
                                .clickable { /*viewModel.onAction(HomeAction.TabSelected(index))*/ }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.Black else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            /*if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
            } else {*/
            items(questions, key = { it.id }) { question ->
                QuestionCard(
                    question = question,
                    onClick = { onQuestionClick(question.id) },
                    onBookmark = { /*viewModel.onAction(HomeAction.ToggleBookmark(question.id))*/ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
//            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

data class QuestionUiModel(
    val id: String,
    val authorName: String,
    val timeAgo: Int,
    val isBookmarked: Boolean,
    val category: String,
    val title: String,
    val preview: String,
    val codeSnippet: String?,
    val tags: List<String>,
    val likes: Int,
    val answers: Int,
)

@Preview
@Composable
private fun PreviewViewQuestionsScreen() {
    DevzTheme {
        ViewQuestionsScreen(
            onAddQuestion = {},
            onQuestionClick = {},
            onProfileClick = {}
        )
    }
}