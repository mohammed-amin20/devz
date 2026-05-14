package com.mohamed.devz.navigation.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohamed.devz.feature.notification.presentation.NotificationsScreen
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileScreen
import com.mohamed.devz.feature.question.presentation.question_details.components.Bg
import com.mohamed.devz.feature.question.presentation.view_questions.ViewQuestionsScreen
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun HomeScreen(
    navigateToQuestionDetails: (String) -> Unit,
    navigateToAddEditQuestion: (String?) -> Unit,
    navigateToEditProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

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
            onClick = { selectedIndex = 0 },
            content = {
                ViewQuestionsScreen(
                    onQuestionClick = { questionId -> navigateToQuestionDetails(questionId) },
                    onProfileClick = { selectedIndex = 3 },
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
            onClick = { selectedIndex = 2 },
            content = {
                NotificationsScreen()
            }
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Rounded.Person,
            index = 3,
            onClick = { selectedIndex = 3 },
            content = {
                ProfileScreen(
                    onEditProfile = navigateToEditProfile,
                    onQuestionClick = { questionId -> navigateToQuestionDetails(questionId) },
                )
            }
        )
    )

    BackHandler {
        if (selectedIndex != 0) {
            selectedIndex = 0
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
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
                        .background(Bg.copy(0.9f))
                        .navigationBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navigationItems.forEachIndexed { index, item ->
                        NavIconButton(
                            icon = item.icon,
                            isSelected = index == selectedIndex,
                            onClick = item.onClick
                        )
                    }
                }
            }
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
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    DevzTheme {
        HomeScreen(
            navigateToQuestionDetails = {},
            navigateToAddEditQuestion = {},
            navigateToEditProfile = {}
        )
    }
}