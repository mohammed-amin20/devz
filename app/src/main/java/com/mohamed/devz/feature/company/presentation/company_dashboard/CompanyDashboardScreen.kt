package com.mohamed.devz.feature.company.presentation.company_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.company.presentation.edit_company_profile.EditCompanyProfileOverlay
import com.mohamed.devz.feature.core.presentation.components.DevzBrandHeader
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CompanyDashboardScreen(
    onPostJob: () -> Unit,
    onJobClick: (Int) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CompanyDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        if (uiState.selectedTab != pagerState.currentPage) {
            viewModel.onAction(CompanyDashboardAction.SelectTab(pagerState.currentPage))
        }
    }

    LaunchedEffect(uiState.selectedTab) {
        if (pagerState.currentPage != uiState.selectedTab) {
            pagerState.animateScrollToPage(uiState.selectedTab)
        }
    }

    CompanyDashboardContent(
        state = uiState,
        onRefresh = { viewModel.onAction(CompanyDashboardAction.Refresh) },
        onPostJob = onPostJob,
        onJobClick = onJobClick,
        onLogout = onLogout,
        modifier = modifier,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
    )
}

@Composable
fun CompanyDashboardContent(
    state: CompanyDashboardState,
    onRefresh: () -> Unit,
    onPostJob: () -> Unit,
    onJobClick: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(pageCount = { 2 }),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DevzBrandHeader()
                Row {
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.company_edit_profile),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.company_logout),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error!!.asString(),
                        onRetry = onRefresh,
                        retryLabel = stringResource(R.string.action_retry),
                    )
                }

                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = onRefresh,
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize(),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = state.isRefreshing,
                                state = pullRefreshState,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    if (state.logoUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = state.logoUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                                    CircleShape,
                                                ),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.Business,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                                .padding(12.dp)
                                                .size(32.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = state.companyName,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if (state.website.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Language,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = state.website,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 13.sp,
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val statusColor = when (state.subscriptionStatus) {
                                        "active" -> MaterialTheme.extendedColors.success
                                        "pending" -> MaterialTheme.extendedColors.warning
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                    Text(
                                        text = subscriptionStatusLabel(state.subscriptionStatus),
                                        color = statusColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            PrimaryTabRow(
                                selectedTabIndex = pagerState.currentPage,
                                containerColor = MaterialTheme.colorScheme.background,
                                divider = {},
                            ) {
                                Tab(
                                    selected = pagerState.currentPage == 0,
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                                    text = {
                                        Text(
                                            stringResource(R.string.company_tab_offered, state.offeredJobs.size),
                                            fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (pagerState.currentPage == 0) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        )
                                    },
                                )
                                Tab(
                                    selected = pagerState.currentPage == 1,
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                                    text = {
                                        Text(
                                            stringResource(R.string.company_tab_reserved, state.reservedJobs.size),
                                            fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (pagerState.currentPage == 1) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        )
                                    },
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            ) { page ->
                                val currentJobs = if (page == 0) state.offeredJobs else state.reservedJobs

                                if (currentJobs.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = stringResource(
                                                if (page == 0) R.string.company_empty_offered else R.string.company_empty_reserved
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp,
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize(),
                                    ) {
                                        items(currentJobs, key = { it.id }) { job ->
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceContainer,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onJobClick(job.id) },
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        Text(
                                                            text = job.title,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.weight(1f),
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        val jobStatusColor = jobStatusColor(job.status)
                                                        Text(
                                                            text = job.status.replaceFirstChar { it.uppercase() },
                                                            color = jobStatusColor,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier
                                                                .background(jobStatusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                                        )
                                                    }
                                                    if (job.createdAt.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = formatTimestamp(job.createdAt),
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontSize = 11.sp,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        item { Spacer(modifier = Modifier.height(80.dp)) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.subscriptionStatus == "active" && !state.isLoading) {
            FloatingActionButton(
                onClick = onPostJob,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.company_profile_post_job))
            }
        }

        AnimatedVisibility(
            visible = showLogoutDialog,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(250)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 8.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.company_logout_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.company_logout_message),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Button(
                                onClick = { showLogoutDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            ) {
                                Text(stringResource(R.string.company_logout_cancel), fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    onLogout()
                                    showLogoutDialog = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                ),
                            ) {
                                Text(stringResource(R.string.company_logout_confirm), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        if (showEditProfileDialog) {
            EditCompanyProfileOverlay(
                visible = true,
                onDismiss = { showEditProfileDialog = false },
                onSaved = {
                    showEditProfileDialog = false
                    onRefresh()
                },
            )
        }
    }
}

@Composable
private fun subscriptionStatusLabel(status: String): String = stringResource(
    when (status) {
        "active" -> R.string.company_status_active
        "pending" -> R.string.company_status_pending
        "expired" -> R.string.company_status_expired
        else -> R.string.company_status_pending
    }
)

@Composable
private fun jobStatusColor(status: String): Color = when (status) {
    "approved", "active", "offered" -> MaterialTheme.extendedColors.success
    "pending" -> MaterialTheme.extendedColors.warning
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

private val previewDashboardState = CompanyDashboardState(
    companyName = "Acme Labs",
    logoUrl = "",
    website = "https://acme.dev",
    description = "We build developer tools.",
    subscriptionStatus = "active",
    offeredJobs = listOf(
        CompanyJobUiModel(
            id = 1,
            title = "Senior Android Developer",
            status = "approved",
            createdAt = "2024-01-01T00:00:00Z",
        )
    ),
    reservedJobs = listOf(
        CompanyJobUiModel(
            id = 2,
            title = "Backend Engineer",
            status = "pending",
            createdAt = "2024-01-02T00:00:00Z",
        )
    ),
    selectedTab = 0,
    isLoading = false,
)

@Preview
@Composable
private fun PreviewCompanyDashboardDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        CompanyDashboardContent(
            state = previewDashboardState,
            onRefresh = {},
            onPostJob = {},
            onJobClick = {},
            onLogout = {},
        )
    }
}

@Preview
@Composable
private fun PreviewCompanyDashboardLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        CompanyDashboardContent(
            state = previewDashboardState,
            onRefresh = {},
            onPostJob = {},
            onJobClick = {},
            onLogout = {},
        )
    }
}
