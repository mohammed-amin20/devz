package com.mohamed.devz.feature.question.presentation.view_questions

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.components.DevzBrandHeader
import com.mohamed.devz.feature.question.presentation.view_questions.components.QuestionCard
import com.mohamed.devz.feature.question.presentation.view_questions.util.QuestionFeedUiModel
import com.mohamed.devz.feature.report.presentation.ReportSheet
import com.mohamed.devz.feature.report.presentation.ReportTarget
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.components.ShimmerBox
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class FeedItem {
    data class QuestionItem(val question: QuestionFeedUiModel) : FeedItem()
    data object AdBanner : FeedItem()
}

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

    ViewQuestionsContent(
        state = uiState,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        modifier = modifier,
        onSearchQueryChanged = { viewModel.onAction(ViewQuestionsAction.SearchQueryChanged(it)) },
        onTabSelected = { viewModel.onAction(ViewQuestionsAction.TabSelected(it)) },
        onLoadNextPage = { viewModel.onAction(ViewQuestionsAction.LoadNextPage) },
        onRefresh = { viewModel.onAction(ViewQuestionsAction.Refresh) },
        onQuestionClick = onQuestionClick,
        onAuthorClick = onAuthorClick,
        onShowReport = { viewModel.onAction(ViewQuestionsAction.ShowReport(it)) },
        onDismissReport = { viewModel.onAction(ViewQuestionsAction.DismissReport) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewQuestionsContent(
    state: ViewQuestionsState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    onSearchQueryChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onLoadNextPage: () -> Unit,
    onRefresh: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onAuthorClick: (Int) -> Unit,
    onShowReport: (ReportTarget) -> Unit,
    onDismissReport: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        Column(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.lg)) {
            Spacer(Modifier.height(MaterialTheme.spacing.lg))

            DevzBrandHeader()

            Spacer(Modifier.height(MaterialTheme.spacing.md))

            DevzTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = stringResource(R.string.feed_search_hint),
                leadingIcon = Icons.Filled.Search,
                singleLine = true,
            )

            Spacer(Modifier.height(MaterialTheme.spacing.md))

            val tabs = listOf(
                stringResource(R.string.feed_tab_for_you),
                stringResource(R.string.feed_tab_following),
            )
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage, matchContentSize = true)
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
                            onTabSelected(index)
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        },
                    )
                }
            }

            Spacer(Modifier.height(MaterialTheme.spacing.sm))
        }

        if (state.error != null && state.questions.isEmpty() && !state.isLoading) {
            ErrorState(
                message = state.error!!.asString(),
                onRetry = onRefresh,
                retryLabel = stringResource(R.string.action_retry),
            )
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> ForYouFeed(
                        uiState = state,
                        onLoadMore = onLoadNextPage,
                        onRefresh = onRefresh,
                        onQuestionClick = onQuestionClick,
                        onAuthorClick = onAuthorClick,
                        onReport = onShowReport,
                    )
                    1 -> FollowingFeed(
                        uiState = state,
                        onLoadMore = onLoadNextPage,
                        onRefresh = onRefresh,
                        onQuestionClick = onQuestionClick,
                        onAuthorClick = onAuthorClick,
                        onReport = onShowReport,
                    )
                }
            }
        }

        state.reportTarget?.let { target ->
            ReportSheet(
                target = target,
                onDismiss = onDismissReport,
            )
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
    onReport: (ReportTarget) -> Unit,
) {
    val feedItems = remember(uiState.questions, uiState.pinnedQuestions, uiState.isPro) {
        val pinned = uiState.pinnedQuestions.map { FeedItem.QuestionItem(it) }
        val pinnedIds = uiState.pinnedQuestions.map { it.id }.toSet()
        val regular = if (uiState.isPro) {
            uiState.questions.filter { it.id !in pinnedIds }.map { FeedItem.QuestionItem(it) }
        } else {
            buildList<FeedItem> {
                uiState.questions.filter { it.id !in pinnedIds }.forEachIndexed { index, question ->
                    add(FeedItem.QuestionItem(question))
                    if ((index + 1) % 5 == 0 && index < uiState.questions.size - 1) {
                        add(FeedItem.AdBanner)
                    }
                }
            }
        }
        pinned + regular
    }

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
                color = MaterialTheme.colorScheme.primary,
            )
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Spacer(Modifier.height(MaterialTheme.spacing.xs))
            }

            when {
                uiState.isLoading || (uiState.isRefreshing && uiState.questions.isEmpty()) -> {
                    item { FeedSkeleton() }
                }
                uiState.questions.isEmpty() && uiState.noTechMatches -> {
                    item {
                        FeedEmptyState(
                            icon = Icons.Filled.Lightbulb,
                            title = stringResource(R.string.feed_empty_tech_title),
                            subtitle = stringResource(R.string.feed_empty_tech_subtitle),
                        )
                    }
                }
                uiState.questions.isEmpty() && uiState.isNotFollowingAnyone -> {
                    item {
                        FeedEmptyState(
                            icon = Icons.Filled.PersonSearch,
                            title = stringResource(R.string.feed_empty_following_title),
                            subtitle = stringResource(R.string.feed_empty_following_subtitle),
                        )
                    }
                }
                uiState.questions.isEmpty() && !uiState.isNotFollowingAnyone -> {
                    item {
                        FeedEmptyState(
                            icon = Icons.Filled.Forum,
                            title = stringResource(R.string.feed_empty_no_questions_title),
                            subtitle = stringResource(R.string.feed_empty_no_questions_subtitle),
                        )
                    }
                }
                else -> {
                    items(
                        items = feedItems,
                        key = {
                            when (it) {
                                is FeedItem.QuestionItem -> "q_${it.question.id}"
                                is FeedItem.AdBanner -> "ad_${it.hashCode()}"
                            }
                        },
                    ) { item ->
                        Column(modifier = Modifier.animateItem()) {
                            when (item) {
                                is FeedItem.QuestionItem -> {
                                    QuestionCard(
                                        question = item.question,
                                        onClick = { onQuestionClick(item.question.id) },
                                        onAuthorClick = { onAuthorClick(item.question.authorAccountId) },
                                        onReport = if (uiState.currentAccountId != 0 && item.question.authorAccountId != uiState.currentAccountId) {
                                            {
                                                onReport(
                                                    ReportTarget(
                                                        reportedType = "question",
                                                        reportedId = item.question.id,
                                                        preview = item.question.title,
                                                    )
                                                )
                                            }
                                        } else null,
                                    )
                                    Spacer(Modifier.height(MaterialTheme.spacing.md))
                                }
                                is FeedItem.AdBanner -> {
                                    AdBannerItem()
                                    Spacer(Modifier.height(MaterialTheme.spacing.md))
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        com.mohamed.devz.ui.components.ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            shape = RoundedCornerShape(16.dp),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
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
    onReport: (ReportTarget) -> Unit,
) {
    val feedItems = remember(uiState.questions, uiState.pinnedQuestions, uiState.isPro) {
        val pinnedIds = uiState.pinnedQuestions.map { it.id }.toSet()
        if (uiState.isPro) {
            uiState.questions.filter { it.id !in pinnedIds }.map { FeedItem.QuestionItem(it) }
        } else {
            buildList<FeedItem> {
                uiState.questions.filter { it.id !in pinnedIds }.forEachIndexed { index, question ->
                    add(FeedItem.QuestionItem(question))
                    if ((index + 1) % 5 == 0 && index < uiState.questions.size - 1) {
                        add(FeedItem.AdBanner)
                    }
                }
            }
        }
    }

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
                color = MaterialTheme.colorScheme.primary,
            )
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            when {
                uiState.isLoading || (uiState.isRefreshing && uiState.questions.isEmpty()) -> {
                    item { FeedSkeleton() }
                }
                uiState.questions.isEmpty() && uiState.isNotFollowingAnyone -> {
                    item {
                        FeedEmptyState(
                            icon = Icons.Filled.PersonSearch,
                            title = stringResource(R.string.feed_empty_following_title),
                            subtitle = stringResource(R.string.feed_empty_following_subtitle),
                        )
                    }
                }
                uiState.questions.isEmpty() && !uiState.isNotFollowingAnyone -> {
                    item {
                        FeedEmptyState(
                            icon = Icons.Filled.Forum,
                            title = stringResource(R.string.feed_empty_no_questions_title),
                            subtitle = stringResource(R.string.feed_empty_no_questions_subtitle),
                        )
                    }
                }
                else -> {
                    items(
                        items = feedItems,
                        key = {
                            when (it) {
                                is FeedItem.QuestionItem -> "q_${it.question.id}"
                                is FeedItem.AdBanner -> "ad_${it.hashCode()}"
                            }
                        },
                    ) { item ->
                        Column(modifier = Modifier.animateItem()) {
                            when (item) {
                                is FeedItem.QuestionItem -> {
                                    QuestionCard(
                                        question = item.question,
                                        onClick = { onQuestionClick(item.question.id) },
                                        onAuthorClick = { onAuthorClick(item.question.authorAccountId) },
                                        onReport = if (uiState.currentAccountId != 0 && item.question.authorAccountId != uiState.currentAccountId) {
                                            {
                                                onReport(
                                                    ReportTarget(
                                                        reportedType = "question",
                                                        reportedId = item.question.id,
                                                        preview = item.question.title,
                                                    )
                                                )
                                            }
                                        } else null,
                                    )
                                    Spacer(Modifier.height(MaterialTheme.spacing.md))
                                }
                                is FeedItem.AdBanner -> {
                                    AdBannerItem()
                                    Spacer(Modifier.height(MaterialTheme.spacing.md))
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        com.mohamed.devz.ui.components.ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            shape = RoundedCornerShape(16.dp),
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun FeedEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    val animAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOut),
        label = "emptyAlpha",
    )
    val animScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseOutBack),
        label = "emptyScale",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animAlpha)
            .scale(animScale),
    ) {
        EmptyState(icon = icon, title = title, message = subtitle)
    }
}

@Composable
private fun FeedSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(3) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(modifier = Modifier.padding(MaterialTheme.spacing.lg)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerBox(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(50),
                        )
                        Spacer(Modifier.size(MaterialTheme.spacing.sm))
                        ShimmerBox(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.4f),
                            shape = RoundedCornerShape(6.dp),
                        )
                    }
                    Spacer(Modifier.height(MaterialTheme.spacing.md))
                    ShimmerBox(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(6.dp),
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.sm))
                    ShimmerBox(
                        modifier = Modifier
                            .height(13.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.xs))
                    ShimmerBox(
                        modifier = Modifier
                            .height(13.dp)
                            .fillMaxWidth(0.7f),
                        shape = RoundedCornerShape(6.dp),
                    )
                }
            }
            Spacer(Modifier.height(MaterialTheme.spacing.md))
        }
    }
}

@Composable
private fun AdBannerItem() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.feed_ad_space),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showSystemUi = true, name = "Feed — Dark")
@Composable
private fun PreviewViewQuestionsScreenDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ViewQuestionsContent(
            state = ViewQuestionsState(
                questions = listOf(previewQuestion, previewQuestion2),
                pinnedQuestions = listOf(previewQuestionPinned),
                isLoading = false,
                currentAccountId = 2,
            ),
            pagerState = rememberPagerState(pageCount = { 2 }),
            coroutineScope = rememberCoroutineScope(),
            onSearchQueryChanged = {},
            onTabSelected = {},
            onLoadNextPage = {},
            onRefresh = {},
            onQuestionClick = {},
            onAuthorClick = {},
            onShowReport = {},
            onDismissReport = {},
        )
    }
}

@Preview(showSystemUi = true, name = "Feed — Light")
@Composable
private fun PreviewViewQuestionsScreenLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ViewQuestionsContent(
            state = ViewQuestionsState(
                questions = listOf(previewQuestion, previewQuestion2),
                pinnedQuestions = listOf(previewQuestionPinned),
                isLoading = false,
                currentAccountId = 2,
            ),
            pagerState = rememberPagerState(pageCount = { 2 }),
            coroutineScope = rememberCoroutineScope(),
            onSearchQueryChanged = {},
            onTabSelected = {},
            onLoadNextPage = {},
            onRefresh = {},
            onQuestionClick = {},
            onAuthorClick = {},
            onShowReport = {},
            onDismissReport = {},
        )
    }
}

private val previewQuestion = QuestionFeedUiModel(
    id = 1,
    authorAccountId = 10,
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    timeAgo = "2h",
    category = "Android",
    title = "How to handle deep links in Compose Navigation?",
    preview = "I'm trying to navigate to a specific screen using a deep link but it keeps opening the Home screen.",
    codeSnippet = null,
    tags = listOf("compose", "navigation"),
    likes = 12,
    answers = 3,
    isAuthorPro = true,
)

private val previewQuestion2 = QuestionFeedUiModel(
    id = 2,
    authorAccountId = 11,
    authorName = "Sara Khaled",
    authorAvatarUrl = "",
    timeAgo = "5h",
    category = "Kotlin",
    title = "What is the difference between apply and also?",
    preview = "Both are scope functions but I can't tell when to use which.",
    codeSnippet = "val l = list.map {\n    it * 2\n}.also { println(it) }",
    tags = listOf("kotlin", "scope-functions"),
    likes = 8,
    answers = 5,
)

private val previewQuestionPinned = QuestionFeedUiModel(
    id = 3,
    authorAccountId = 10,
    authorName = "Ahmed Ali",
    authorAvatarUrl = "",
    timeAgo = "1d",
    category = "Compose",
    title = "Pinned: Best practices for state hoisting",
    preview = "A pinned question to show off the pinned feed section.",
    codeSnippet = null,
    tags = listOf("compose"),
    likes = 50,
    answers = 20,
    isPinned = true,
)