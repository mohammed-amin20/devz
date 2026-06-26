package com.mohamed.devz.navigation.components.home

import android.graphics.Color.argb
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamed.devz.feature.notification.presentation.NotificationsScreen
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileScreen
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.QSurfaceLow
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsScreen
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun HomeScreen(
    navigateToQuestionDetails: (Int) -> Unit,
    navigateToAddEditQuestion: (Int?) -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToProfile: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    switchToProfileTab: Boolean = false,
    profileRefreshCounter: Int = 0,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val selectedIndex by viewModel.selectedIndex.collectAsStateWithLifecycle()
    val currentAccountId by viewModel.currentAccountId.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    LaunchedEffect(switchToProfileTab) {
        if (switchToProfileTab) {
            viewModel.onSelectedIndexChange(3)
        }
    }

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
            title = "Home",
            icon = Icons.Rounded.Home,
            index = 0,
            onClick = { viewModel.onSelectedIndexChange(0) },
            content = {
                ViewQuestionsScreen(
                    onQuestionClick = { questionId -> navigateToQuestionDetails(questionId) },
                    onAuthorClick = { accountId ->
                        if (accountId == currentAccountId) {
                            viewModel.onSelectedIndexChange(3)
                        } else {
                            navigateToProfile(accountId)
                        }
                    },
                    onNavigateToEditProfile = navigateToEditProfile,
                )
            }
        ),
        NavigationItem(
            title = "Add",
            icon = Icons.Rounded.Add,
            index = 1,
            onClick = { navigateToAddEditQuestion(null) },
            content = null
        ),
        NavigationItem(
            title = "Notifications",
            icon = Icons.Rounded.Notifications,
            index = 2,
            onClick = { viewModel.onSelectedIndexChange(2) },
            content = {
                NotificationsScreen(
                    onNotificationClick = navigateToQuestionDetails
                )
            }
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Rounded.Person,
            index = 3,
            onClick = { viewModel.onSelectedIndexChange(3) },
            content = {
                ProfileScreen(
                    onEditProfile = navigateToEditProfile,
                    onQuestionClick = { questionId -> navigateToQuestionDetails(questionId) },
                    onAnswerClick = { questionId -> navigateToQuestionDetails(questionId) },
                    onLogout = onLogout,
                    refreshTrigger = profileRefreshCounter,
                    onFullScreenChanged = { isFullScreenImage = it },
                    onDialogVisibilityChanged = { isDialogOpen = it },
                    navigateUp = { viewModel.onSelectedIndexChange(0) }
                )
            }
        )
    )

    BackHandler {
        if (selectedIndex != 0) {
            viewModel.onSelectedIndexChange(0)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = if (!isFullScreenImage && !isDialogOpen) {
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                    startY = 0f,
                                    endY = size.height
                                )
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(QSurfaceLow)
                            .height(64.dp)
                            .imePadding()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navigationItems.forEachIndexed { index, item ->
                            NavIconButton(
                                icon = item.icon,
                                isSelected = index == selectedIndex,
                                onClick = item.onClick,
                                badgeCount = if (index == 2) unreadCount else 0,
                            )
                        }
                    }
                }
            }
        } else {
            {}
        },
        modifier = modifier
    ) { innerPadding ->
        navigationItems[selectedIndex]
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
    isAccent: Boolean = false,
    badgeCount: Int = 0,
    onClick: () -> Unit,
) {
    val tint = when {
        isAccent && isSelected -> Color(0xFF44D8F1)
        isSelected -> Color(0xFF44D8F1)
        else -> Color(0xFF869396)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(50))
            .drawBehind {
                if (isSelected) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(16f, 0f, 0f, argb(20, 68, 216, 241))
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
            modifier = Modifier
                .size(if (isAccent && isSelected) 30.dp else 26.dp)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    DevzTheme {
        HomeScreen(
            navigateToQuestionDetails = { _ -> },
            navigateToAddEditQuestion = { _ -> },
            navigateToEditProfile = {},
            navigateToProfile = { _ -> },
            onLogout = {}
        )
    }
}