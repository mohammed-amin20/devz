package com.mohamed.devz.feature.profile.presentation.view_profile.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamed.devz.R
import com.mohamed.devz.feature.core.presentation.components.ProBadge
import com.mohamed.devz.ui.theme.spacing

/** Display-only progression thresholds. Purely a UI convention — not persisted. */
private val TIER_THRESHOLDS = listOf(0, 100, 300, 600, 1000, 2000)

@Composable
fun GamificationCard(
    points: Int,
    isPro: Boolean,
    modifier: Modifier = Modifier,
) {
    val safePoints = points.coerceAtLeast(0)
    val levelIndex = TIER_THRESHOLDS.indexOfLast { safePoints >= it }.coerceAtLeast(0)
    val level = levelIndex + 1
    val isMaxLevel = levelIndex >= TIER_THRESHOLDS.lastIndex

    val currentThreshold = TIER_THRESHOLDS[levelIndex]
    val nextThreshold = if (isMaxLevel) currentThreshold else TIER_THRESHOLDS[levelIndex + 1]
    val span = (nextThreshold - currentThreshold).coerceAtLeast(1)
    val targetProgress = if (isMaxLevel) 1f else ((safePoints - currentThreshold).toFloat() / span).coerceIn(0f, 1f)
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 700),
        label = "gamifProgress",
    )
    val pointsToNext = (nextThreshold - safePoints).coerceAtLeast(0)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.gamif_achievement),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.titleLarge,
                )
                if (isPro) ProBadge()
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.gamif_level, level),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(tierNameRes(levelIndex)),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = stringResource(R.string.gamif_points, safePoints),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            Text(
                text = if (isMaxLevel) {
                    stringResource(R.string.gamif_max_level)
                } else {
                    stringResource(R.string.gamif_next_level, pointsToNext)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun tierNameRes(levelIndex: Int): Int = when (levelIndex) {
    0 -> R.string.gamif_tier_newcomer
    1 -> R.string.gamif_tier_contributor
    2 -> R.string.gamif_tier_specialist
    3 -> R.string.gamif_tier_expert
    4 -> R.string.gamif_tier_master
    else -> R.string.gamif_tier_legend
}
