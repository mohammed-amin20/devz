package com.mohamed.devz.feature.company.presentation.company_profile

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
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
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.components.ErrorState
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode
import com.mohamed.devz.ui.theme.extendedColors

@Composable
fun CompanyProfileScreen(
    onJobClick: (Int) -> Unit,
    onPostJob: () -> Unit,
    modifier: Modifier = Modifier,
    navAccountId: Int? = null,
    navigateUp: () -> Unit,
    viewModel: CompanyProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navAccountId) {
        viewModel.onAction(CompanyProfileAction.SetTargetAccountId(navAccountId))
    }

    BackHandler { navigateUp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            uiState.error != null -> {
                ErrorState(
                    message = uiState.error!!.asString(),
                    onRetry = { viewModel.onAction(CompanyProfileAction.Refresh) },
                    retryLabel = stringResource(R.string.action_retry),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        CompanyHeader(
                            uiState = uiState,
                            onBack = navigateUp,
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            PrimaryTabRow(
                                selectedTabIndex = uiState.selectedTab,
                                containerColor = MaterialTheme.colorScheme.background,
                                divider = {},
                            ) {
                                Tab(
                                    selected = uiState.selectedTab == 0,
                                    onClick = { viewModel.onAction(CompanyProfileAction.SelectTab(0)) },
                                    text = {
                                        Text(
                                            stringResource(R.string.company_profile_tab_posted, uiState.postedJobs.size),
                                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 0) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        )
                                    },
                                )
                                Tab(
                                    selected = uiState.selectedTab == 1,
                                    onClick = { viewModel.onAction(CompanyProfileAction.SelectTab(1)) },
                                    text = {
                                        Text(
                                            stringResource(R.string.company_profile_tab_filled, uiState.filledJobs.size),
                                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 1) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                        )
                                    },
                                )
                            }
                        }
                    }

                    val currentJobs = if (uiState.selectedTab == 0) uiState.postedJobs else uiState.filledJobs
                    if (currentJobs.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(
                                    if (uiState.selectedTab == 0) {
                                        R.string.company_profile_empty_posted
                                    } else {
                                        R.string.company_profile_empty_filled
                                    }
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    } else {
                        items(currentJobs, key = { it.id }) { job ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
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
                                        val isOpen = uiState.selectedTab == 0
                                        val chipColor = if (isOpen) {
                                            MaterialTheme.extendedColors.success
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                        Text(
                                            text = stringResource(
                                                if (isOpen) R.string.company_profile_open else R.string.company_profile_filled
                                            ),
                                            color = chipColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(chipColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
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
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (uiState.isOwnProfile && !uiState.isLoading) {
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
    }
}

@Composable
private fun CompanyHeader(
    uiState: CompanyProfileUiState,
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val gold = MaterialTheme.extendedColors.pro
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.company_profile_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(3.dp, gold, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.logoUrl.isNotBlank()) {
                        AsyncImage(
                            model = uiState.logoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(104.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Business,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                }
                if (uiState.isVerified) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(gold)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.company_profile_verified),
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = uiState.companyName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (uiState.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = gold,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            if (uiState.industry.isNotBlank() || uiState.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = listOf(uiState.industry, uiState.location)
                        .filter { it.isNotBlank() }
                        .joinToString(" \u00b7 "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }

            val summary = uiState.bio.ifBlank { uiState.description }
            if (summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = summary,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (uiState.website.isNotBlank() || uiState.twitterUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (uiState.website.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                uriHandler.openUri(normalizeUrl(uiState.website))
                            },
                        ) {
                            Icon(
                                Icons.Filled.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.company_profile_website),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                    if (uiState.twitterUrl.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                uriHandler.openUri(normalizeUrl(uiState.twitterUrl))
                            },
                        ) {
                            Icon(
                                Icons.Filled.AlternateEmail,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.company_profile_twitter),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatItem(
                        value = uiState.totalJobs.toString(),
                        label = stringResource(R.string.company_profile_stat_jobs),
                    )
                    StatItem(
                        value = uiState.totalApplicants.toString(),
                        label = stringResource(R.string.company_profile_stat_applicants),
                    )
                    StatItem(
                        value = uiState.filledJobs.size.toString(),
                        label = stringResource(R.string.company_profile_stat_filled),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
        )
    }
}

private fun normalizeUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
}

@Preview
@Composable
private fun PreviewCompanyProfileDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        CompanyProfileScreen(onJobClick = {}, onPostJob = {}, navigateUp = {})
    }
}

@Preview
@Composable
private fun PreviewCompanyProfileLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        CompanyProfileScreen(onJobClick = {}, onPostJob = {}, navigateUp = {})
    }
}
