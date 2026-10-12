package com.mohamed.devz.feature.admin.presentation.manage_announcements

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.presentation.util.UiText
import com.mohamed.devz.feature.core.presentation.util.formatTimestampUtcPlus3
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.components.EmptyState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

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

    ManageAnnouncementsContent(
        state = uiState,
        onNavigateUp = onNavigateUp,
        onTitleChanged = { viewModel.onAction(ManageAnnouncementsAction.TitleChanged(it)) },
        onMessageChanged = { viewModel.onAction(ManageAnnouncementsAction.MessageChanged(it)) },
        onCreateAnnouncement = { viewModel.onAction(ManageAnnouncementsAction.CreateAnnouncement) },
        onShowCreateDialog = { viewModel.onAction(ManageAnnouncementsAction.ShowCreateDialog) },
        onDismissCreateDialog = { viewModel.onAction(ManageAnnouncementsAction.DismissCreateDialog) },
        onDeleteAnnouncement = { viewModel.onAction(ManageAnnouncementsAction.DeleteAnnouncement(it)) },
        onConfirmDelete = { viewModel.onAction(ManageAnnouncementsAction.ConfirmDelete(it)) },
        onDismissDeleteDialog = { viewModel.onAction(ManageAnnouncementsAction.DismissDeleteDialog) },
        onRefresh = { viewModel.onAction(ManageAnnouncementsAction.Refresh) },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnnouncementsContent(
    state: ManageAnnouncementsState,
    onNavigateUp: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onMessageChanged: (String) -> Unit,
    onCreateAnnouncement: () -> Unit,
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onDeleteAnnouncement: (Notification) -> Unit,
    onConfirmDelete: (Notification) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    if (state.showDeleteDialog && state.targetDeleteNotification != null) {
        val parts = state.targetDeleteNotification!!.message.split("\n", limit = 2)
        val title = parts.getOrElse(0) { "" }
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_delete_announcement_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_delete_announcement_message, title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmDelete(state.targetDeleteNotification!!) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (state.showCreateDialog) {
        AlertDialog(
            onDismissRequest = onDismissCreateDialog,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    stringResource(R.string.admin_new_announcement_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DevzTextField(
                        value = state.createTitle,
                        onValueChange = onTitleChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.admin_field_title),
                        singleLine = true,
                    )
                    DevzTextField(
                        value = state.createMessage,
                        onValueChange = onMessageChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.admin_field_message),
                        singleLine = false,
                        maxLines = 6,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onCreateAnnouncement,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(stringResource(R.string.action_create), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissCreateDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_announcements_title),
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
            Button(
                onClick = onShowCreateDialog,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Text(
                    stringResource(R.string.admin_create_announcement),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
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
                state.announcements.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Filled.Campaign,
                        title = stringResource(R.string.admin_empty_announcements),
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
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(state.announcements, key = { it.id }) { notification ->
                                SystemNotificationCard(
                                    notification = notification,
                                    onDelete = { onDeleteAnnouncement(notification) },
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
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            if (notification.createdAt.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatTimestampUtcPlus3(notification.createdAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text(stringResource(R.string.action_delete), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageAnnouncementsDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        ManageAnnouncementsContent(
            state = ManageAnnouncementsState(
                announcements = listOf(previewNotification),
                isLoading = false,
            ),
            onNavigateUp = {},
            onTitleChanged = {},
            onMessageChanged = {},
            onCreateAnnouncement = {},
            onShowCreateDialog = {},
            onDismissCreateDialog = {},
            onDeleteAnnouncement = {},
            onConfirmDelete = {},
            onDismissDeleteDialog = {},
            onRefresh = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewManageAnnouncementsLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        ManageAnnouncementsContent(
            state = ManageAnnouncementsState(
                announcements = listOf(previewNotification),
                isLoading = false,
            ),
            onNavigateUp = {},
            onTitleChanged = {},
            onMessageChanged = {},
            onCreateAnnouncement = {},
            onShowCreateDialog = {},
            onDismissCreateDialog = {},
            onDeleteAnnouncement = {},
            onConfirmDelete = {},
            onDismissDeleteDialog = {},
            onRefresh = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

private val previewNotification = Notification(
    id = 1,
    typeId = 1,
    userId = 1,
    actorId = 1,
    questionId = 0,
    answerId = null,
    type = "system",
    message = "System update\nWe are improving the app performance.",
    isRead = false,
    createdAt = "2024-01-01T00:00:00Z",
    isGlobal = true,
)