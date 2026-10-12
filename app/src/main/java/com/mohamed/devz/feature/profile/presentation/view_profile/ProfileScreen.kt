package com.mohamed.devz.feature.profile.presentation.view_profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.components.DevzBrandHeader
import com.mohamed.devz.feature.core.presentation.components.ProBadge
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.feature.profile.presentation.view_profile.components.EmptyTabContent
import com.mohamed.devz.feature.profile.presentation.view_profile.components.FollowListSheet
import com.mohamed.devz.feature.profile.presentation.view_profile.components.GamificationCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileAnswerCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.ProfileQuestionCard
import com.mohamed.devz.feature.profile.presentation.view_profile.components.StatCard
import com.mohamed.devz.feature.profile.presentation.view_profile.util.ProfileJobApplicationUiModel
import com.mohamed.devz.feature.report.presentation.ReportMenu
import com.mohamed.devz.feature.report.presentation.ReportSheet
import com.mohamed.devz.feature.report.presentation.ReportTarget
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors
import com.mohamed.devz.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onAnswerClick: (Int) -> Unit = onQuestionClick,
    refreshTrigger: Int = 0,
    onFullScreenChanged: (Boolean) -> Unit = {},
    onDialogVisibilityChanged: (Boolean) -> Unit = {},
    onProfileClick: (Int) -> Unit = {},
    onAdminPanelClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onJobClick: (Int) -> Unit = {},
    navAccountId: Int? = null,
    navigateUp: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showImagePreview by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger > 0) {
            viewModel.onAction(ProfileAction.Refresh)
        }
    }
    LaunchedEffect(navAccountId) {
        viewModel.onAction(ProfileAction.SetTargetAccountId(navAccountId))
        viewModel.onAction(ProfileAction.LoadApplications)
    }
    LaunchedEffect(showImagePreview, uiState.showFollowersDialog, uiState.showFollowingDialog) {
        if (!showImagePreview && !uiState.showFollowersDialog && !uiState.showFollowingDialog) {
            onFullScreenChanged(false)
            onDialogVisibilityChanged(false)
        }
    }
    LaunchedEffect(uiState.showFollowersDialog, uiState.showFollowingDialog) {
        if (uiState.showFollowersDialog || uiState.showFollowingDialog) {
            onDialogVisibilityChanged(true)
        }
    }

    BackHandler {
        when {
            showImagePreview -> {
                showImagePreview = false
                onFullScreenChanged(false)
            }
            uiState.showFollowersDialog || uiState.showFollowingDialog -> {
                viewModel.onAction(ProfileAction.DismissDialog)
            }
            else -> navigateUp()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.profile == null -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null && uiState.profile == null -> {
                    ErrorState(
                        message = uiState.error!!.asString(),
                        onRetry = { viewModel.onAction(ProfileAction.Refresh) },
                        retryLabel = stringResource(R.string.action_retry),
                    )
                }

                else -> {
                    var selectedTab by remember { mutableIntStateOf(0) }
                    val tabs = buildList {
                        add(
                            stringResource(
                                if (uiState.isOwnProfile) R.string.profile_tab_my_questions
                                else R.string.profile_tab_their_questions
                            )
                        )
                        add(
                            stringResource(
                                if (uiState.isOwnProfile) R.string.profile_tab_my_answers
                                else R.string.profile_tab_their_answers
                            )
                        )
                        if (uiState.isOwnProfile) {
                            add(stringResource(R.string.profile_tab_applications))
                        }
                    }

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
                                color = MaterialTheme.colorScheme.primary,
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
                                        .background(MaterialTheme.colorScheme.background),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (!uiState.isOwnProfile) {
                                            IconButton(onClick = navigateUp) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = stringResource(R.string.profile_back_desc),
                                                    tint = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        DevzBrandHeader()
                                    }
                                    if (uiState.isOwnProfile) {
                                        IconButton(onClick = onSettingsClick) {
                                            Icon(
                                                Icons.Filled.Settings,
                                                contentDescription = stringResource(R.string.settings_open),
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    } else if (uiState.id != 0) {
                                        ReportMenu(
                                            onReport = {
                                                viewModel.onAction(
                                                    ProfileAction.ShowReport(
                                                        ReportTarget(
                                                            reportedType = "user",
                                                            reportedId = uiState.id,
                                                            preview = uiState.profile?.fullName
                                                                ?: "@${uiState.profile?.username ?: "user"}",
                                                        )
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            // ── Avatar ────────────────────────────────────────────
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                val isPro = uiState.profile?.isPro == true
                                val proColor = MaterialTheme.extendedColors.pro
                                Box(
                                    modifier = Modifier.clickable {
                                        showImagePreview = true
                                        onFullScreenChanged(true)
                                    },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isPro) 118.dp else 110.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isPro) Brush.radialGradient(
                                                    colors = listOf(
                                                        proColor.copy(alpha = 0.4f),
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                        Color.Transparent
                                                    )
                                                ) else Brush.radialGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .shadow(
                                                elevation = if (isPro) 32.dp else 24.dp,
                                                spotColor = if (isPro) proColor else MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .then(
                                                if (isPro) Modifier.border(
                                                    3.dp,
                                                    Brush.sweepGradient(
                                                        listOf(proColor, MaterialTheme.colorScheme.primary, proColor)
                                                    ),
                                                    CircleShape
                                                ) else Modifier.border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                    CircleShape
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val avatarUrl = uiState.profile?.imageUrl
                                        if (avatarUrl != null && avatarUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = avatarUrl,
                                                contentDescription = stringResource(R.string.profile_avatar_desc),
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                Icons.Filled.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(52.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                Text(
                                    text = uiState.profile?.fullName.orEmpty(),
                                    color = MaterialTheme.colorScheme.onSurface,
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
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (uiState.profile?.isPro == true) {
                                            ProBadge()
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(R.string.profile_no_username),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 14.sp,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                val bio = uiState.profile?.bio
                                if (bio != null && bio.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = bio,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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

                            // ── Action button + Followers/Following row ──────────
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (uiState.isOwnProfile) {
                                        Button(
                                            onClick = onEditProfile,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text(
                                                text = stringResource(R.string.profile_edit_profile),
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
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                                ),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.profile_following),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.onAction(ProfileAction.ToggleFollow(uiState.id)) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                                )
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.profile_follow),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                    FollowCountBox(
                                        count = uiState.followersCount,
                                        label = stringResource(R.string.profile_followers),
                                        onClick = { viewModel.onAction(ProfileAction.ShowFollowers) },
                                    )
                                    FollowCountBox(
                                        count = uiState.followingCount,
                                        label = stringResource(R.string.profile_following),
                                        onClick = { viewModel.onAction(ProfileAction.ShowFollowing) },
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // ── Admin Panel ───────────────────────────────────────
                            if (uiState.isOwnProfile && uiState.profile?.isAdmin == true) {
                                item {
                                    OutlinedButton(
                                        onClick = onAdminPanelClick,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary,
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        ),
                                    ) {
                                        Text(
                                            text = "\uD83D\uDEE0\uFE0F " + stringResource(R.string.profile_admin_panel),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            // ── Gamification ──────────────────────────────────────
                            item {
                                GamificationCard(
                                    points = uiState.profile?.points?.toIntOrNull() ?: 0,
                                    isPro = uiState.profile?.isPro == true,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // ── Stats grid ────────────────────────────────────────
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    StatCard(
                                        label = stringResource(R.string.profile_stat_answers),
                                        value = "${uiState.profile?.answerCount ?: 0}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = stringResource(R.string.profile_stat_questions),
                                        value = "${uiState.profile?.questionCount ?: 0}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = stringResource(R.string.profile_stat_accepted),
                                        value = uiState.profile?.acceptedRate ?: "0%",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // ── Skills & Social Links (collapsible) ───────────────
                            item {
                                var sectionsExpanded by remember { mutableStateOf(false) }
                                val skills = uiState.profile?.skills ?: emptyList()
                                val socialLinks = listOfNotNull(
                                    uiState.profile?.let { p ->
                                        if (p.githubUrl.isNotBlank()) Triple(Icons.Filled.Code, "GitHub", p.githubUrl) else null
                                    },
                                    uiState.profile?.let { p ->
                                        if (p.linkedInUrl.isNotBlank()) Triple(Icons.Filled.Link, "LinkedIn", p.linkedInUrl) else null
                                    },
                                    uiState.profile?.let { p ->
                                        if (p.websiteUrl.isNotBlank()) Triple(Icons.Filled.Language, "Website", p.websiteUrl) else null
                                    },
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    AnimatedVisibility(
                                        visible = sectionsExpanded,
                                        enter = expandVertically(animationSpec = tween(350)) + fadeIn(tween(250)),
                                        exit = shrinkVertically(animationSpec = tween(350)) + fadeOut(tween(250))
                                    ) {
                                        Column {
                                            if (skills.isNotEmpty()) {
                                                SectionLabel(stringResource(R.string.profile_specialized_skills))
                                                Spacer(modifier = Modifier.height(12.dp))
                                                FlowRow(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    skills.forEach { skill ->
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                                                .border(
                                                                    1.dp,
                                                                    MaterialTheme.colorScheme.outlineVariant,
                                                                    RoundedCornerShape(8.dp)
                                                                )
                                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                        ) {
                                                            Text(
                                                                text = skill,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                fontSize = 13.sp,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            if (socialLinks.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(28.dp))
                                                SectionLabel(stringResource(R.string.profile_social_links))
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
                                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            Icon(
                                                                icon,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                            Text(
                                                                text = label,
                                                                color = MaterialTheme.colorScheme.onSurface,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                            Spacer(modifier = Modifier.weight(1f))
                                                            Icon(
                                                                Icons.Filled.Language,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (skills.isNotEmpty() || socialLinks.isNotEmpty()) {
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
                                                text = stringResource(
                                                    if (sectionsExpanded) R.string.profile_show_less
                                                    else R.string.profile_show_more
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = if (sectionsExpanded) Icons.Filled.KeyboardArrowUp
                                                else Icons.Filled.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // ── Tabs ──────────────────────────────────────────────
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
                                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary
                                                        else Color.Transparent
                                                    )
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // ── Tab content ──────────────────────────────────────
                            when (selectedTab) {
                                0 -> {
                                    if (uiState.myQuestions.isEmpty()) {
                                        item {
                                            EmptyTabContent(
                                                icon = Icons.AutoMirrored.Filled.Help,
                                                title = stringResource(R.string.profile_empty_questions_title),
                                                subtitle = stringResource(R.string.profile_empty_questions_subtitle)
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
                                }
                                1 -> {
                                    if (uiState.myAnswers.isEmpty()) {
                                        item {
                                            EmptyTabContent(
                                                icon = Icons.Filled.Forum,
                                                title = stringResource(R.string.profile_empty_answers_title),
                                                subtitle = stringResource(R.string.profile_empty_answers_subtitle)
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
                                2 -> {
                                    when {
                                        uiState.isLoadingApplications -> {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 32.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                        uiState.applicationsError != null -> {
                                            item {
                                                EmptyTabContent(
                                                    icon = Icons.Filled.Warning,
                                                    title = stringResource(R.string.profile_applications_error_title),
                                                    subtitle = uiState.applicationsError?.asString()
                                                        ?: stringResource(R.string.something_went_wrong),
                                                )
                                            }
                                        }
                                        uiState.myApplications.isEmpty() -> {
                                            item {
                                                EmptyTabContent(
                                                    icon = Icons.Filled.Business,
                                                    title = stringResource(R.string.profile_empty_applications_title),
                                                    subtitle = stringResource(R.string.profile_empty_applications_subtitle)
                                                )
                                            }
                                        }
                                        else -> {
                                            items(uiState.myApplications, key = { it.id }) { app ->
                                                ApplicationCard(
                                                    application = app,
                                                    onClick = { onJobClick(app.jobId) },
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                            }
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

        // ── Image preview ─────────────────────────────────────────────────────
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
                            .shadow(32.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = stringResource(R.string.profile_avatar_desc),
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
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = stringResource(R.string.profile_avatar_desc),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }

        FollowListSheet(
            visible = uiState.showFollowersDialog,
            title = stringResource(R.string.profile_followers),
            countLabel = stringResource(R.string.profile_followers_count, uiState.followersCount),
            emptyText = stringResource(R.string.profile_followers_empty),
            accounts = uiState.followerAccounts,
            isLoading = uiState.isLoadingDialog,
            onDismiss = { viewModel.onAction(ProfileAction.DismissDialog) },
            onProfileClick = { accountId ->
                viewModel.onAction(ProfileAction.DismissDialog)
                onProfileClick(accountId)
            },
        )

        FollowListSheet(
            visible = uiState.showFollowingDialog,
            title = stringResource(R.string.profile_following),
            countLabel = stringResource(R.string.profile_following_count, uiState.followingCount),
            emptyText = stringResource(R.string.profile_following_empty),
            accounts = uiState.followingAccounts,
            isLoading = uiState.isLoadingDialog,
            onDismiss = { viewModel.onAction(ProfileAction.DismissDialog) },
            onProfileClick = { accountId ->
                viewModel.onAction(ProfileAction.DismissDialog)
                onProfileClick(accountId)
            },
        )

        uiState.reportTarget?.let { target ->
            ReportSheet(
                target = target,
                onDismiss = { viewModel.onAction(ProfileAction.DismissReport) },
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun FollowCountBox(
    count: Int,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun ApplicationCard(
    application: ProfileJobApplicationUiModel,
    onClick: () -> Unit = {},
) {
    val statusColor = when (application.status) {
        "accepted" -> MaterialTheme.extendedColors.success
        "rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.extendedColors.warning
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = application.jobTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                )
                if (application.companyName.isNotBlank()) {
                    Text(
                        text = application.companyName,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (application.createdAt.isNotBlank()) {
                    Text(
                        text = formatTimestamp(application.createdAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.15f),
            ) {
                Text(
                    text = application.status.replaceFirstChar { it.uppercase() },
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(name = "Profile — Dark")
@Composable
private fun PrevProfileDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ProfileScreen(
            onEditProfile = {},
            onQuestionClick = { _ -> },
            onAnswerClick = { _ -> },
            navigateUp = {}
        )
    }
}

@Preview(name = "Profile — Light")
@Composable
private fun PrevProfileLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ProfileScreen(
            onEditProfile = {},
            onQuestionClick = { _ -> },
            onAnswerClick = { _ -> },
            navigateUp = {}
        )
    }
}
