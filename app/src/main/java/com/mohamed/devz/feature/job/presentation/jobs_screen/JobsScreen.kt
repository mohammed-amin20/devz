package com.mohamed.devz.feature.job.presentation.jobs_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.job.presentation.jobs_screen.components.JobCard
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.QBg
import com.mohamed.devz.ui.theme.QOnSurface
import com.mohamed.devz.ui.theme.QOutline
import com.mohamed.devz.ui.theme.QSurfaceHigh
import com.mohamed.devz.ui.theme.TextGray

private val FILTER_OPTIONS = listOf(
    null to "All",
    "full-time" to "Full-time",
    "part-time" to "Part-time",
    "remote" to "Remote",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    onJobClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JobsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QBg)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jobs",
            color = QOnSurface,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FILTER_OPTIONS) { (type, label) ->
                val isSelected = uiState.selectedFilter == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) CyanPrimary else QSurfaceHigh)
                        .clickable { viewModel.onAction(JobsAction.FilterByType(type)) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) QBg else TextGray,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CyanPrimary)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.error!!.asString(),
                        color = QOutline,
                        fontSize = 14.sp,
                    )
                }
            }

            uiState.filteredJobs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No jobs found",
                        color = QOutline,
                        fontSize = 16.sp,
                    )
                }
            }

            else -> {
                val pullRefreshState = rememberPullToRefreshState()
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.onAction(JobsAction.Refresh) },
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
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(uiState.filteredJobs, key = { it.id }) { job ->
                            JobCard(
                                job = job,
                                onClick = { onJobClick(job.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
