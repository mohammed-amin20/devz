package com.mohamed.devz.navigation.components.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamed.devz.R
import com.mohamed.devz.feature.notification.presentation.NotificationType
import com.mohamed.devz.feature.notification.presentation.NotificationUiModel
import com.mohamed.devz.feature.notification.presentation.NotificationsScreen
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileScreen
import com.mohamed.devz.feature.job.presentation.jobs_screen.JobsScreen
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsScreen
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyDashboardScreen
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyDashboardState
import com.mohamed.devz.feature.company.presentation.company_dashboard.CompanyDashboardViewModel
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun HomeScreen(
    navigateToQuestionDetails: (Int) -> Unit,
    navigateToAddEditQuestion: (Int?) -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToProfile: (Int) -> Unit,
    navigateToJobDetail: (Int) -> Unit,
    navigateToCompanyJobDetail: (Int) -> Unit = {},
    navigateToPostJob: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    switchToProfileTab: Boolean = false,
    profileRefreshCounter: Int = 0,
    navigateToAdminDashboard: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val selectedIndex by viewModel.selectedIndex.collectAsStateWithLifecycle()
    val currentAccountId by viewModel.currentAccountId.collectAsStateWithLifecycle()
    val accountType by viewModel.accountType.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    val companyViewModel: CompanyDashboardViewModel? =
        if (accountType == "company") hiltViewModel() else null
    val companyUiState: CompanyDashboardState? =
        companyViewModel?.let { it.uiState.collectAsStateWithLifecycle().value }

    val isHomeLoading = accountType == null ||
        (accountType == "company" && (companyUiState == null || companyUiState.isLoading))

    LaunchedEffect(switchToProfileTab) {
        if (switchToProfileTab) {
            viewModel.onSelectedIndexChange(4)
        }
    }

    if (isHomeLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    when (accountType) {
        "company" -> {
            CompanyDashboardScreen(
                onPostJob = navigateToPostJob,
                onJobClick = navigateToCompanyJobDetail,
                onLogout = onLogout,
                modifier = modifier,
                viewModel = companyViewModel!!,
            )
            return
        }
    }

    HomeContent(
        selectedIndex = selectedIndex,
        currentAccountId = currentAccountId,
        unreadCount = unreadCount,
        modifier = modifier,
        refreshTrigger = profileRefreshCounter,
        onTabSelected = viewModel::onSelectedIndexChange,
        onUnreadChanged = viewModel::refreshUnreadCount,
        onBackToFeed = { viewModel.onSelectedIndexChange(0) },
        onAddQuestion = { navigateToAddEditQuestion(null) },
        onQuestionClick = navigateToQuestionDetails,
        onAuthorClick = { accountId ->
            if (accountId == currentAccountId) {
                viewModel.onSelectedIndexChange(4)
            } else {
                navigateToProfile(accountId)
            }
        },
        onNotificationClick = { notification ->
            if (notification.type == NotificationType.FOLLOWER) {
                navigateToProfile(notification.actorId)
            } else {
                navigateToQuestionDetails(notification.questionId)
            }
        },
        onJobClick = navigateToJobDetail,
        onCompanyClick = navigateToProfile,
        onProfileClick = { accountId ->
            if (accountId == currentAccountId) {
                viewModel.onSelectedIndexChange(4)
            } else {
                navigateToProfile(accountId)
            }
        },
        onEditProfile = navigateToEditProfile,
        onAdminPanelClick = navigateToAdminDashboard,
        onSettingsClick = navigateToSettings,
    )
}

@Composable
fun HomeContent(
    selectedIndex: Int,
    currentAccountId: Int,
    unreadCount: Int,
    modifier: Modifier = Modifier,
    refreshTrigger: Int = 0,
    onTabSelected: (Int) -> Unit,
    onUnreadChanged: () -> Unit,
    onBackToFeed: () -> Unit,
    onAddQuestion: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onAuthorClick: (Int) -> Unit,
    onNotificationClick: (NotificationUiModel) -> Unit,
    onJobClick: (Int) -> Unit,
    onCompanyClick: (Int) -> Unit,
    onProfileClick: (Int) -> Unit,
    onEditProfile: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var isFullScreenImage by remember { mutableStateOf(false) }
    var isDialogOpen by remember { mutableStateOf(false) }

    data class NavigationItem(
        val title: String,
        val icon: ImageVector,
        val index: Int,
        val onClick: () -> Unit,
        val content: (@Composable (Modifier) -> Unit)?,
    )

    val navigationItems = listOf(
        NavigationItem(
            title = stringResource(R.string.nav_home),
            icon = Icons.Rounded.Home,
            index = 0,
            onClick = { onTabSelected(0) },
            content = {
                ViewQuestionsScreen(
                    onQuestionClick = { questionId -> onQuestionClick(questionId) },
                    onAuthorClick = onAuthorClick,
                )
            }
        ),
        NavigationItem(
            title = stringResource(R.string.nav_add),
            icon = Icons.Rounded.Add,
            index = 1,
            onClick = { onAddQuestion() },
            content = null
        ),
        NavigationItem(
            title = stringResource(R.string.nav_notifications),
            icon = Icons.Rounded.Notifications,
            index = 2,
            onClick = { onTabSelected(2) },
            content = {
                NotificationsScreen(
                    onNotificationClick = onNotificationClick,
                    onUnreadChanged = onUnreadChanged,
                )
            }
        ),
        NavigationItem(
            title = stringResource(R.string.nav_jobs),
            icon = Icons.Rounded.WorkHistory,
            index = 3,
            onClick = { onTabSelected(3) },
            content = {
                JobsScreen(
                    onJobClick = onJobClick,
                    onCompanyClick = onCompanyClick,
                )
            }
        ),
        NavigationItem(
            title = stringResource(R.string.nav_profile),
            icon = Icons.Rounded.Person,
            index = 4,
            onClick = { onTabSelected(4) },
            content = {
                ProfileScreen(
                    onEditProfile = onEditProfile,
                    onQuestionClick = onQuestionClick,
                    onAnswerClick = onQuestionClick,
                    onJobClick = onJobClick,
                    refreshTrigger = refreshTrigger,
                    onFullScreenChanged = { isFullScreenImage = it },
                    onDialogVisibilityChanged = { isDialogOpen = it },
                    onAdminPanelClick = onAdminPanelClick,
                    onSettingsClick = onSettingsClick,
                    onProfileClick = onProfileClick,
                    navigateUp = onBackToFeed,
                )
            }
        )
    )

    BackHandler(enabled = selectedIndex != 0) {
        onBackToFeed()
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = if (!isFullScreenImage && !isDialogOpen) {
            {
                val scrimColor = MaterialTheme.colorScheme.scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, scrimColor.copy(alpha = 0.5f)),
                                    startY = 0f,
                                    endY = size.height
                                )
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .height(64.dp)
                            .imePadding()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(0, 2).forEach { index ->
                            val item = navigationItems[index]
                            NavIconButton(
                                icon = item.icon,
                                isSelected = index == selectedIndex,
                                onClick = item.onClick,
                                badgeCount = if (index == 2) unreadCount else 0,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.width(76.dp))

                        listOf(3, 4).forEach { index ->
                            val item = navigationItems[index]
                            NavIconButton(
                                icon = item.icon,
                                isSelected = index == selectedIndex,
                                onClick = item.onClick,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    AddQuestionFab(
                        onClick = onAddQuestion,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-4).dp),
                    )
                }
            }
        } else {
            {}
        },
        modifier = modifier
    ) { innerPadding ->
        navigationItems.first { it.index == selectedIndex }
            .content?.let { content ->
                content(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
    }
}

@Composable
private fun NavIconButton(
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
        label = "navIconTint",
    )
    val iconSize by animateDpAsState(
        targetValue = 26.dp,
        label = "navIconSize",
    )
    val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f).toArgb()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(50))
            .drawBehind {
                if (isSelected) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(16f, 0f, 0f, glowColor)
                        }
                        canvas.nativeCanvas.drawCircle(center.x, center.y, 20.dp.toPx(), paint)
                    }
                }
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AddQuestionFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "addFabScale",
    )
    val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = glowColor,
                spotColor = glowColor,
            )
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFF00BCD4), Color(0xFF44D8F1))))
            .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onClick,
            ),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = stringResource(R.string.nav_add),
            tint = Color(0xFF00363E),
            modifier = Modifier.size(32.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    DevzTheme {
        HomeContent(
            selectedIndex = 0,
            currentAccountId = 1,
            unreadCount = 3,
            onTabSelected = {},
            onUnreadChanged = {},
            onBackToFeed = {},
            onAddQuestion = {},
            onQuestionClick = {},
            onAuthorClick = {},
            onNotificationClick = {},
            onJobClick = {},
            onCompanyClick = {},
            onProfileClick = {},
            onEditProfile = {},
            onAdminPanelClick = {},
            onSettingsClick = {},
        )
    }
}