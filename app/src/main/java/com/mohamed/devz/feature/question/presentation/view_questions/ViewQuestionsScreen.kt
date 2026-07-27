package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.question.presentation.view_questions.components.QuestionCard
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewQuestionsScreen(
    onQuestionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onAuthorClick: (Int) -> Unit = {},
    viewModel: ViewQuestionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(Unit) {
        viewModel.onAction(ViewQuestionsAction.LoadInitialQuestions)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (uiState.selectedTab != pagerState.currentPage) {
            viewModel.onAction(ViewQuestionsAction.TabSelected(pagerState.currentPage))
        }
    }

    LaunchedEffect(uiState.selectedTab) {
        if (pagerState.currentPage != uiState.selectedTab) {
            pagerState.animateScrollToPage(uiState.selectedTab)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QBg)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onAction(ViewQuestionsAction.SearchQueryChanged(it)) },
                placeholder = {
                    Text(
                        "Query the collective intelligence...",
                        color = TextSubtle,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextSubtle) },
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(12.dp))

            val tabs = listOf("For You", "Following")
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = DevzCard,
                contentColor = CyanPrimary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        color = CyanPrimary,
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
                    )
                },
                divider = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (pagerState.currentPage == index) CyanPrimary else TextSubtle,
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ForYouFeed(
                    uiState = uiState,
                    onLoadMore = { viewModel.onAction(ViewQuestionsAction.LoadNextPage) },
                    onRefresh = { viewModel.onAction(ViewQuestionsAction.Refresh) },
                    onQuestionClick = onQuestionClick,
                    onAuthorClick = onAuthorClick,
                )
                1 -> FollowingFeed(
                    uiState = uiState,
                    onLoadMore = { viewModel.onAction(ViewQuestionsAction.LoadNextPage) },
                    onRefresh = { viewModel.onAction(ViewQuestionsAction.Refresh) },
                    onQuestionClick = onQuestionClick,
                    onAuthorClick = onAuthorClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForYouFeed(
    uiState: ViewQuestionsState,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onAuthorClick: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.questions.isNotEmpty()) {
            onLoadMore()
        }
    }

    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = uiState.isRefreshing,
                state = pullRefreshState,
                color = CyanPrimary,
            )
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (uiState.isLoading || (uiState.isRefreshing && uiState.questions.isEmpty())) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
            } else if (uiState.questions.isEmpty() && uiState.noTechMatches) {
                item {
                    EmptyStateAnimated(
                        icon = { Icon(Icons.Filled.Lightbulb, null, tint = CyanPrimary, modifier = Modifier.size(36.dp)) },
                        title = "No matching questions",
                        subtitle = "Add technologies to your profile so we can\nshow you relevant questions.",
                    )
                }
            } else if (uiState.questions.isEmpty() && uiState.isNotFollowingAnyone) {
                item {
                    EmptyStateAnimated(
                        icon = { Icon(Icons.Filled.PersonSearch, null, tint = CyanPrimary, modifier = Modifier.size(36.dp)) },
                        title = "Your feed is empty",
                        subtitle = "Follow developers to see their questions here.\nSearch for people or browse questions to find\ndevelopers who share your interests.",
                    )
                }
            } else if (uiState.questions.isEmpty() && !uiState.isNotFollowingAnyone) {
                item {
                    EmptyStateAnimated(
                        icon = { Icon(Icons.Filled.Forum, null, tint = CyanPrimary, modifier = Modifier.size(36.dp)) },
                        title = "No questions yet",
                        subtitle = "Questions from developers you follow\nwill appear here.",
                    )
                }
            } else {
                items(uiState.questions, key = { it.id }) { question ->
                    QuestionCard(
                        question = question,
                        onClick = { onQuestionClick(question.id) },
                        onAuthorClick = { onAuthorClick(question.authorAccountId) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CyanPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowingFeed(
    uiState: ViewQuestionsState,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onAuthorClick: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.questions.isNotEmpty()) {
            onLoadMore()
        }
    }

    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = uiState.isRefreshing,
                state = pullRefreshState,
                color = CyanPrimary,
            )
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (uiState.isLoading || (uiState.isRefreshing && uiState.questions.isEmpty())) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
            } else if (uiState.questions.isEmpty() && uiState.isNotFollowingAnyone) {
                item {
                    EmptyStateAnimated(
                        icon = { Icon(Icons.Filled.PersonSearch, null, tint = CyanPrimary, modifier = Modifier.size(36.dp)) },
                        title = "Your feed is empty",
                        subtitle = "Follow developers to see their questions here.\nSearch for people or browse questions to find\ndevelopers who share your interests.",
                    )
                }
            } else if (uiState.questions.isEmpty() && !uiState.isNotFollowingAnyone) {
                item {
                    EmptyStateAnimated(
                        icon = { Icon(Icons.Filled.Forum, null, tint = CyanPrimary, modifier = Modifier.size(36.dp)) },
                        title = "No questions yet",
                        subtitle = "Questions from developers you follow\nwill appear here.",
                    )
                }
            } else {
                items(uiState.questions, key = { it.id }) { question ->
                    QuestionCard(
                        question = question,
                        onClick = { onQuestionClick(question.id) },
                        onAuthorClick = { onAuthorClick(question.authorAccountId) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CyanPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun EmptyStateAnimated(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
) {
    val animAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOut),
    )
    val animScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseOutBack),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D3333),
                        Color(0xFF0A1A1A),
                        Color(0xFF060D0D)
                    ),
                    center = Offset(0.5f, 0.4f),
                    radius = 1200f
                )
            )
            .padding(vertical = 56.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(animAlpha)
                .scale(animScale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyanPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                title,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                subtitle,
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewViewQuestionsScreen() {
    DevzTheme {
        ViewQuestionsScreen(
            onQuestionClick = {}
        )
    }
}
