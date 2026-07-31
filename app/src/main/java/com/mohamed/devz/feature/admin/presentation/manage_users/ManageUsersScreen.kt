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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

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
            containerColor = DevzCard,
            title = {
                Text("Ban User", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to ban @${uiState.targetBanAccount!!.username}? They will no longer be able to post.",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmBan(uiState.targetBanAccount!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = QError)
                ) {
                    Text("Ban", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissBanDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    if (uiState.showProDialog && uiState.targetProAccount != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageUsersAction.DismissProDialog) },
            containerColor = DevzCard,
            title = {
                Text(
                    if (uiState.targetProAccount!!.isPro) "Remove Pro" else "Make Pro",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = if (uiState.targetProAccount!!.isPro)
                        "Are you sure you want to remove Pro from @${uiState.targetProAccount!!.username}?"
                    else
                        "Are you sure you want to make @${uiState.targetProAccount!!.username} a Pro member?",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmPro(uiState.targetProAccount!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(
                        if (uiState.targetProAccount!!.isPro) "Remove" else "Make Pro",
                        color = TextWhite,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissProDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    if (uiState.showAdminDialog && uiState.targetAdminAccount != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageUsersAction.DismissAdminDialog) },
            containerColor = DevzCard,
            title = {
                Text(
                    if (uiState.targetAdminAccount!!.isAdmin) "Remove Admin" else "Make Admin",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = if (uiState.targetAdminAccount!!.isAdmin)
                        "Are you sure you want to remove admin privileges from @${uiState.targetAdminAccount!!.username}?"
                    else
                        "Are you sure you want to make @${uiState.targetAdminAccount!!.username} an admin?",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageUsersAction.ConfirmAdmin(uiState.targetAdminAccount!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(
                        if (uiState.targetAdminAccount!!.isAdmin) "Remove" else "Make Admin",
                        color = TextWhite,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageUsersAction.DismissAdminDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    Scaffold(
        containerColor = QBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Manage Users", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onAction(ManageUsersAction.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search users...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextGray)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = Color(0xFF2A3A3A),
                    focusedContainerColor = DevzCard,
                    unfocusedContainerColor = DevzCard,
                    cursorColor = CyanPrimary,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                ),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == 0,
                    onClick = { viewModel.onAction(ManageUsersAction.FilterAll) },
                    label = { Text("All", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = CyanPrimary,
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 0,
                    ),
                )
                FilterChip(
                    selected = uiState.selectedFilter == 1,
                    onClick = { viewModel.onAction(ManageUsersAction.FilterBanned) },
                    label = { Text("Banned", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = QError.copy(alpha = 0.2f),
                        selectedLabelColor = QError,
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = QError.copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 1,
                    ),
                )
                FilterChip(
                    selected = uiState.selectedFilter == 2,
                    onClick = { viewModel.onAction(ManageUsersAction.FilterPro) },
                    label = { Text("Pro", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = CyanPrimary,
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 2,
                    ),
                )
                FilterChip(
                    selected = uiState.selectedFilter == 3,
                    onClick = { viewModel.onAction(ManageUsersAction.FilterAdmin) },
                    label = { Text("Admin", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = CyanPrimary,
                        containerColor = DevzCard,
                        labelColor = TextGray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF2A3A3A),
                        selectedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                        enabled = true,
                        selected = uiState.selectedFilter == 3,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
                uiState.filteredAccounts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No users found", color = QOnSurfaceVariant, fontSize = 16.sp)
                    }
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
                                color = CyanPrimary,
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
        color = DevzCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f))
                        .border(1.dp, CyanPrimary.copy(alpha = 0.3f), CircleShape),
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
                            tint = CyanPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.fullName,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (account.isPro) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PRO",
                                color = CyanPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (account.isAdmin) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ADMIN",
                                color = CyanPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (account.isBanned) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BANNED",
                                color = QError,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(QError.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "@${account.username}",
                        color = TextGray,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${account.points} pts",
                            color = CyanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF2A3A3A), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            if (isMainAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onShowAdminDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (account.isAdmin) CyanPrimary else TextGray
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (account.isAdmin) CyanPrimary.copy(alpha = 0.5f) else Color(0xFF2A3A3A)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(
                            if (account.isAdmin) "Admin" else "Not Admin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    OutlinedButton(
                        onClick = onShowProDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (account.isPro) CyanPrimary else TextGray
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (account.isPro) CyanPrimary.copy(alpha = 0.5f) else Color(0xFF2A3A3A)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(
                            if (account.isPro) "Pro" else "Not Pro",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (account.isBanned) {
                        OutlinedButton(
                            onClick = onUnban,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        ) {
                            Text("Unban", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onBan,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = QError),
                            border = BorderStroke(1.dp, QError.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        ) {
                            Text("Ban", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onShowProDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (account.isPro) CyanPrimary else TextGray
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (account.isPro) CyanPrimary.copy(alpha = 0.5f) else Color(0xFF2A3A3A)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    ) {
                        Text(
                            if (account.isPro) "Pro" else "Not Pro",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (account.isBanned) {
                        OutlinedButton(
                            onClick = onUnban,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        ) {
                            Text("Unban", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onBan,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = QError),
                            border = BorderStroke(1.dp, QError.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        ) {
                            Text("Ban", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
