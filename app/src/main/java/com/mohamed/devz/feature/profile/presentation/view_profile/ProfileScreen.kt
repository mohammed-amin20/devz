package com.mohamed.devz.feature.profile.presentation.view_profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.profile.presentation.view_profile.components.EmptyTabContent
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileAnswerCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileQuestionCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.StatCard
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onAnswerClick: (Int) -> Unit = onQuestionClick,
    refreshTrigger: Int = 0,
    onFullScreenChanged: (Boolean) -> Unit = {},
    onDialogVisibilityChanged: (Boolean) -> Unit = {},
    navigateUp: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.profileEvent.collect { event ->
            when (event) {
                is ProfileEvent.NavigateToAuth -> onLogout()
            }
        }
    }
    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger > 0) {
            viewModel.onAction(ProfileAction.Refresh)
        }
    }
    LaunchedEffect(showImagePreview, showLogoutDialog) {
        if (!showImagePreview && !showLogoutDialog) {
            onFullScreenChanged(false)
            onDialogVisibilityChanged(false)
        }
    }

    BackHandler {
        if(showImagePreview) {
            showImagePreview = false
            onFullScreenChanged(false)
        } else if(showLogoutDialog) {
            showLogoutDialog = false
            onDialogVisibilityChanged(false)
        } else {
            navigateUp()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(QBg)
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (showLogoutDialog) Modifier.blur(8.dp) else Modifier
                )
        ) {
            when {
            uiState.isLoading && uiState.profile == null -> {
                CircularProgressIndicator(
                    color = CyanPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null && uiState.profile == null -> {
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
                        .fillMaxSize()
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
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(animAlpha)
                            .scale(animScale)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                QError.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = QError,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Oops!",
                            color = QOnSurface,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Something went wrong",
                            color = QOnSurfaceVariant,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = DevzCard
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = QError,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = uiState.error!!.asString(),
                                    color = QOnSurfaceVariant,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onAction(ProfileAction.Refresh) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Try Again",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            else -> {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf(
                    "${if(uiState.isOwnProfile) "MY" else "THEIR"} QUESTIONS",
                    "${if (uiState.isOwnProfile) "MY" else "THEIR"} ANSWERS"
                )

                val pullRefreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = { viewModel.onAction(ProfileAction.Refresh) },
                    state = pullRefreshState,
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = uiState.isLoading,
                            state = pullRefreshState,
                            color = CyanPrimary,
                        )
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    stickyHeader {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(QBg),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!uiState.isOwnProfile) {
                                    IconButton(onClick = navigateUp) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = TextWhite,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
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
                            if (uiState.isOwnProfile) {
                                TextButton(
                                    onClick = {
                                        showLogoutDialog = true
                                        onDialogVisibilityChanged(true)
                                    },
                                ) {
                                    Text(
                                        text = "Log Out",
                                        color = QError,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null,
                                        tint = QError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── Avatar ────────────────────────────────────────────────────
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.clickable {
                                showImagePreview = true
                                onFullScreenChanged(true)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                CyanPrimary.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(
                                        elevation = 24.dp,
                                        spotColor = CyanPrimary,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .background(DevzCard)
                                    .border(2.dp, CyanPrimary.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatarUrl = uiState.profile?.imageUrl
                                if (avatarUrl != null && avatarUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Person,
                                        null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(52.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            text = uiState.profile?.fullName ?: "Alex riv",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val username = uiState.profile?.username
                            if (!username.isNullOrEmpty()) {
                                Text(
                                    text = "@$username",
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "No username",
                                    color = TextGray.copy(alpha = 0.5f),
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${uiState.profile?.points ?: "2,480"} PTS",
                                    color = CyanPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        val bio = uiState.profile?.bio
                        if (bio != null && bio.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = bio,
                                color = TextGray,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ── Action button + Followers/Following row ──────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (uiState.isOwnProfile) {
                                Button(
                                    onClick = onEditProfile,
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyanPrimary,
                                        contentColor = Color(0xFF00363E)
                                    )
                                ) {
                                    Text(
                                        text = "Edit Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            } else {
                                val isFollowing = uiState.isFollowing
                                if (isFollowing) {
                                    OutlinedButton(
                                        onClick = { viewModel.onAction(ProfileAction.ToggleFollow(uiState.id)) },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = TextWhite,
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, TextGray),
                                    ) {
                                        Text(
                                            text = "Following",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.onAction(ProfileAction.ToggleFollow(uiState.id)) },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CyanPrimary,
                                            contentColor = Color(0xFF00363E)
                                        )
                                    ) {
                                        Text(
                                            text = "Follow",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyanPrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.followersCount}",
                                        color = CyanPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Followers",
                                        color = TextGray,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyanPrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.followingCount}",
                                        color = CyanPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Following",
                                        color = TextGray,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ── Stats grid ────────────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                label = "ANSWERS",
                                value = "${uiState.profile?.answerCount ?: 0}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "QUESTIONS",
                                value = "${uiState.profile?.questionCount ?: 0}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "ACCEPTED",
                                value = uiState.profile?.acceptedRate ?: "0%",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ── Skills & Social Links (collapsible) ────────────────────────
                    item {
                        var sectionsExpanded by remember { mutableStateOf(false) }
                        val skills = uiState.profile?.skills
                            ?: listOf(
                                "TypeScript",
                                "React",
                                "Next.js",
                                "Rust",
                                "GraphQL",
                                "WebAssembly",
                                "PostgreSQL",
                                "Tailwind CSS"
                            )
                        val socialLinks = listOfNotNull(
                            uiState.profile?.let { p ->
                                if (p.githubUrl.isNotBlank()) Triple(
                                    Icons.Filled.Code,
                                    "GitHub",
                                    p.githubUrl
                                ) else null
                            },
                            uiState.profile?.let { p ->
                                if (p.linkedInUrl.isNotBlank()) Triple(
                                    Icons.Filled.Link,
                                    "LinkedIn",
                                    p.linkedInUrl
                                ) else null
                            },
                            uiState.profile?.let { p ->
                                if (p.websiteUrl.isNotBlank()) Triple(
                                    Icons.Filled.Language,
                                    "Website",
                                    p.websiteUrl
                                ) else null
                            },
                        )

                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        ) {
                            AnimatedVisibility(
                                visible = sectionsExpanded,
                                enter = expandVertically(animationSpec = tween(350)) + fadeIn(
                                    tween(
                                        250
                                    )
                                ),
                                exit = shrinkVertically(animationSpec = tween(350)) + fadeOut(
                                    tween(
                                        250
                                    )
                                )
                            ) {
                                Column {
                                    if(skills.isNotEmpty()) {
                                        Text(
                                            text = "SPECIALIZED SKILLS",
                                            color = TextGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            skills.forEach { skill ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0XFF2A2A2A))
                                                        .border(
                                                            1.dp,
                                                            Color(0xFF2A3A3A),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(
                                                            horizontal = 12.dp,
                                                            vertical = 6.dp
                                                        )
                                                ) {
                                                    Text(
                                                        text = skill,
                                                        color = TextGray,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (socialLinks.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(28.dp))
                                        Text(
                                            text = "SOCIAL LINKS",
                                            color = TextGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        val uriHandler = LocalUriHandler.current
                                        fun ensureScheme(url: String): String {
                                            return if (url.startsWith("http://") || url.startsWith("https://")) url
                                            else "https://$url"
                                        }
                                        socialLinks.forEach { (icon, label, url) ->
                                            Surface(
                                                onClick = { uriHandler.openUri(ensureScheme(url)) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = DevzCard,
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 14.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        icon,
                                                        contentDescription = null,
                                                        tint = CyanPrimary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = label,
                                                        color = TextWhite,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    Icon(
                                                        Icons.Filled.Language,
                                                        contentDescription = null,
                                                        tint = TextGray.copy(alpha = 0.5f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if(skills.isNotEmpty() || socialLinks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { sectionsExpanded = !sectionsExpanded }
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (sectionsExpanded) "Show less" else "Show more",
                                        color = CyanPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = if (sectionsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // ── Tabs ──────────────────────────────────────────────────────
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            tabs.forEachIndexed { index, tab ->
                                val isSelected = selectedTab == index
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedTab = index },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = tab,
                                        color = if (isSelected) CyanPrimary else TextGray,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        letterSpacing = 1.sp,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isSelected) CyanPrimary else Color.Transparent)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF2A3A3A))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ── Tab content ───────────────────────────────────────────────
                    if (selectedTab == 0) {
                        if (uiState.myQuestions.isEmpty()) {
                            item {
                                EmptyTabContent(
                                    icon = Icons.AutoMirrored.Filled.Help,
                                    title = "No questions yet",
                                    subtitle = "Your questions will appear here"
                                )
                            }
                        } else {
                            items(uiState.myQuestions, key = { it.id }) { question ->
                                ProfileQuestionCard(
                                    question = question,
                                    onClick = { onQuestionClick(question.id) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    } else {
                        if (uiState.myAnswers.isEmpty()) {
                            item {
                                EmptyTabContent(
                                    icon = Icons.Filled.Forum,
                                    title = "No answers yet",
                                    subtitle = "Your answers will appear here"
                                )
                            }
                        } else {
                            items(uiState.myAnswers, key = { it.id }) { answer ->
                                ProfileAnswerCard(
                                    answer = answer,
                                    onClick = { onAnswerClick(answer.questionId) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
            }
        }

        AnimatedVisibility(
            visible = showImagePreview,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        showImagePreview = false
                        onFullScreenChanged(false)
                    },
                contentAlignment = Alignment.Center
            ) {
                val avatarUrl = uiState.profile?.imageUrl
                if (avatarUrl != null && avatarUrl.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(32.dp, CircleShape, spotColor = CyanPrimary)
                            .clip(CircleShape)
                            .border(3.dp, CyanPrimary.copy(alpha = 0.8f), CircleShape)
                            .background(DevzCard),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(280.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.15f))
                            .border(2.dp, CyanPrimary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile picture",
                            tint = CyanPrimary,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showLogoutDialog,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(250))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = DevzCard,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Log out",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Are you sure you want to log out?",
                            color = TextGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    onDialogVisibilityChanged(false)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2A2A2A),
                                    contentColor = TextWhite
                                )
                            ) {
                                Text("Cancel", fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    viewModel.onAction(ProfileAction.Logout)
                                    showLogoutDialog = false
                                    onDialogVisibilityChanged(false)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = QError,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Log out", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PrevProfile() {
    DevzTheme {
        ProfileScreen(
            onEditProfile = {},
            onQuestionClick = { _ -> },
            onAnswerClick = { _ -> },
            onLogout = {},
            navigateUp = {}
        )
    }
}
