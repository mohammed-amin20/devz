package com.mohamed.devz.feature.admin.presentation.manage_announcements

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.presentation.util.formatTimestampUtcPlus3
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnnouncementsScreen(
    onNavigateUp: () -> Unit,
    viewModel: ManageAnnouncementsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (error is UiText.DynamicString) {
                snackbarHostState.showSnackbar(error.value)
            }
        }
    }

    if (uiState.showDeleteDialog && uiState.targetDeleteNotification != null) {
        val parts = uiState.targetDeleteNotification!!.message.split("\n", limit = 2)
        val title = parts.getOrElse(0) { "" }
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageAnnouncementsAction.DismissDeleteDialog) },
            containerColor = DevzCard,
            title = {
                Text("Delete Announcement", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"$title\"? This action cannot be undone.",
                    color = QOnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageAnnouncementsAction.ConfirmDelete(uiState.targetDeleteNotification!!)) },
                    colors = ButtonDefaults.buttonColors(containerColor = QError)
                ) {
                    Text("Delete", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageAnnouncementsAction.DismissDeleteDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    if (uiState.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManageAnnouncementsAction.DismissCreateDialog) },
            containerColor = DevzCard,
            title = {
                Text("New Announcement", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.createTitle,
                        onValueChange = { viewModel.onAction(ManageAnnouncementsAction.TitleChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Title", color = TextGray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = QOnSurfaceVariant,
                            cursorColor = CyanPrimary,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                        ),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = uiState.createMessage,
                        onValueChange = { viewModel.onAction(ManageAnnouncementsAction.MessageChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Message", color = TextGray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = QOnSurfaceVariant,
                            cursorColor = CyanPrimary,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                        ),
                        minLines = 3,
                        maxLines = 6,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ManageAnnouncementsAction.CreateAnnouncement) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Create", color = TextWhite, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ManageAnnouncementsAction.DismissCreateDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            },
        )
    }

    Scaffold(
        containerColor = QBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Manage Announcements", color = TextWhite, fontWeight = FontWeight.Bold)
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
            Button(
                onClick = { viewModel.onAction(ManageAnnouncementsAction.ShowCreateDialog) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
            ) {
                Text("Create Announcement", color = TextWhite, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                }
                uiState.announcements.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No announcements yet", color = QOnSurfaceVariant, fontSize = 16.sp)
                    }
                }
                else -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.onAction(ManageAnnouncementsAction.Refresh) },
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
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(uiState.announcements, key = { it.id }) { notification ->
                                SystemNotificationCard(
                                    notification = notification,
                                    onDelete = { viewModel.onAction(ManageAnnouncementsAction.DeleteAnnouncement(notification)) },
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
private fun SystemNotificationCard(
    notification: Notification,
    onDelete: () -> Unit,
) {
    val parts = notification.message.split("\n", limit = 2)
    val title = parts.getOrElse(0) { "" }
    val body = parts.getOrElse(1) { "" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DevzCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = body,
                color = QOnSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            if (notification.createdAt.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatTimestampUtcPlus3(notification.createdAt),
                    color = TextGray,
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = QError),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QError.copy(alpha = 0.5f)),
                ) {
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
