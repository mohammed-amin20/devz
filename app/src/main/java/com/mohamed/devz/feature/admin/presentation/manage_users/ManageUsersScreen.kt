package com.mohamed.devz.feature.admin.presentation.manage_users

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    onNavigateUp: () -> Unit,
    onUserClick: (Int) -> Unit,
    viewModel: ManageUsersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showBanDialog && uiState.targetBanAccount != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageUsersAction.DismissBanDialog) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_ban_user_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_ban_message, uiState.targetBanAccount!!.username),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmBan(uiState.targetBanAccount!!)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(R.string.admin_action_ban))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissBanDialog) }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (uiState.showProDialog && uiState.targetProAccount != null) {
        val isPro = uiState.targetProAccount!!.isPro
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageUsersAction.DismissProDialog) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(if (isPro) R.string.admin_pro_remove_title else R.string.admin_pro_make_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(
                        if (isPro) R.string.admin_pro_remove_message else R.string.admin_pro_make_message,
                        uiState.targetProAccount!!.username,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmPro(uiState.targetProAccount!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(stringResource(if (isPro) R.string.action_remove else R.string.admin_pro_make_title))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissProDialog) }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (uiState.showAdminDialog && uiState.targetAdminAccount != null) {
        val isAdmin = uiState.targetAdminAccount!!.isAdmin
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageUsersAction.DismissAdminDialog) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(if (isAdmin) R.string.admin_admin_remove_title else R.string.admin_admin_make_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(
                        if (isAdmin) R.string.admin_admin_remove_message else R.string.admin_admin_make_message,
                        uiState.targetAdminAccount!!.username,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmAdmin(uiState.targetAdminAccount!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(stringResource(if (isAdmin) R.string.action_remove else R.string.admin_admin_make_title))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissAdminDialog) }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_users_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            DevzTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onAction(ManageUsersAction.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.admin_search_users),
                leadingIcon = Icons.Filled.Search,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminFilterChip(
                    selected = uiState.selectedFilter == 0,
                    label = stringResource(R.string.admin_filter_all),
                    onClick = { viewModel.onAction(ManageUsersAction.FilterAll) },
                )
                AdminFilterChip(
                    selected = uiState.selectedFilter == 1,
                    label = stringResource(R.string.admin_filter_banned),
                    onClick = { viewModel.onAction(ManageUsersAction.FilterBanned) },
                    accent = MaterialTheme.colorScheme.error,
                )
                AdminFilterChip(
                    selected = uiState.selectedFilter == 2,
                    label = stringResource(R.string.admin_filter_pro),
                    onClick = { viewModel.onAction(ManageUsersAction.FilterPro) },
                )
                AdminFilterChip(
                    selected = uiState.selectedFilter == 3,
                    label = stringResource(R.string.admin_filter_admin),
                    onClick = { viewModel.onAction(ManageUsersAction.FilterAdmin) },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                uiState.filteredAccounts.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Filled.Person,
                        title = stringResource(R.string.admin_empty_users),
                    )
                }
                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.onAction(ManageUsersAction.Refresh) },
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize(),
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
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.filteredAccounts, key = { it.id }) { account ->
                                UserCard(
                                    account = account,
                                    isMainAdmin = uiState.currentAccountIsMainAdmin,
                                    onUserClick = { onUserClick(account.id) },
                                    onBan = { viewModel.onAction(ManageUsersAction.BanUser(account)) },
                                    onUnban = { viewModel.onAction(ManageUsersAction.UnbanUser(account)) },
                                    onShowProDialog = { viewModel.onAction(ManageUsersAction.ShowProDialog(account)) },
                                    onShowAdminDialog = { viewModel.onAction(ManageUsersAction.ShowAdminDialog(account)) },
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = accent.copy(alpha = 0.2f),
            selectedLabelColor = accent,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = accent.copy(alpha = 0.5f),
        ),
    )
}

@Composable
private fun UserCard(
    account: Account,
    isMainAdmin: Boolean,
    onUserClick: () -> Unit,
    onBan: () -> Unit,
    onUnban: () -> Unit,
    onShowProDialog: () -> Unit,
    onShowAdminDialog: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (account.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = account.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.fullName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (account.isPro) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                text = stringResource(R.string.admin_badge_pro),
                                color = MaterialTheme.extendedColors.pro,
                            )
                        }
                        if (account.isAdmin) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                text = stringResource(R.string.admin_badge_admin),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        if (account.isBanned) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                text = stringResource(R.string.admin_badge_banned),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "@${account.username}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.admin_points, account.points),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isMainAdmin) {
                    OutlinedButton(
                        onClick = onShowAdminDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (account.isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (account.isAdmin) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(
                            stringResource(if (account.isAdmin) R.string.admin_role_admin else R.string.admin_role_not_admin),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                OutlinedButton(
                    onClick = onShowProDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (account.isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (account.isPro) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                ) {
                    Text(
                        stringResource(if (account.isPro) R.string.admin_role_pro else R.string.admin_role_not_pro),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (account.isBanned) {
                    OutlinedButton(
                        onClick = onUnban,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(stringResource(R.string.admin_action_unban), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onBan,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(stringResource(R.string.admin_action_ban), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageUsersDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageUsersScreen(onNavigateUp = {}, onUserClick = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageUsersLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageUsersScreen(onNavigateUp = {}, onUserClick = {})
    }
}
