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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.feature.core.presentation.util.formatTimestamp
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOnSurfaceVariant
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextWhite

private val Gold = Color(0xFFE0B94A)

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
            .background(QBg)
            .then(modifier)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = CyanPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = QError,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error!!.asString(),
                        color = QOnSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.onAction(CompanyProfileAction.Refresh) },
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
                        Text("Retry", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                containerColor = QBg,
                            ) {
                                Tab(
                                    selected = uiState.selectedTab == 0,
                                    onClick = { viewModel.onAction(CompanyProfileAction.SelectTab(0)) },
                                    text = {
                                        Text(
                                            "Posted (${uiState.postedJobs.size})",
                                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 0) CyanPrimary else TextGray,
                                        )
                                    },
                                )
                                Tab(
                                    selected = uiState.selectedTab == 1,
                                    onClick = { viewModel.onAction(CompanyProfileAction.SelectTab(1)) },
                                    text = {
                                        Text(
                                            "Filled (${uiState.filledJobs.size})",
                                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.selectedTab == 1) CyanPrimary else TextGray,
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
                                text = if (uiState.selectedTab == 0) "No open jobs" else "No filled jobs yet",
                                color = QOnSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    } else {
                        items(currentJobs, key = { it.id }) { job ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DevzCard,
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
                                            color = TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (uiState.selectedTab == 0) "Open" else "Filled",
                                            color = if (uiState.selectedTab == 0) Color(0xFF4CAF50) else TextGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(
                                                    if (uiState.selectedTab == 0) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                                    else TextGray.copy(alpha = 0.15f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                        )
                                    }
                                    if (job.createdAt.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatTimestamp(job.createdAt),
                                            color = TextGray,
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
                containerColor = CyanPrimary,
                contentColor = Color(0xFF00363E),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Post New Job")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF114545), Color(0xFF0A1F1F), QBg)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
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
                        .background(DevzCard)
                        .border(3.dp, Gold, CircleShape),
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
                            tint = CyanPrimary,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                }
                if (uiState.isVerified) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Gold)
                            .border(2.dp, QBg, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Verified",
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = uiState.companyName,
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (uiState.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Gold,
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
                    color = QOnSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }

            val summary = uiState.bio.ifBlank { uiState.description }
            if (summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = summary,
                    color = QOnSurface,
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
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Website",
                                color = CyanPrimary,
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
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Twitter",
                                color = CyanPrimary,
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
                color = DevzCard,
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
                        label = "Jobs",
                    )
                    StatItem(
                        value = uiState.totalApplicants.toString(),
                        label = "Applicants",
                    )
                    StatItem(
                        value = uiState.filledJobs.size.toString(),
                        label = "Filled",
                    )
                    StatItem(
                        value = "%.1f".format(uiState.rating),
                        label = "Rating",
                        showStar = true,
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
    showStar: Boolean = false,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showStar) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = value,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextGray,
            fontSize = 11.sp,
        )
    }
}

private fun normalizeUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
}
