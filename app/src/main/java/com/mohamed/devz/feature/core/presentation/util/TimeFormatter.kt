package com.mohamed.devz.feature.core.presentation.util

import kotlin.time.Instant
import kotlin.time.Clock

fun formatRelativeTime(isoTimestamp: String?): String {
    if (isoTimestamp.isNullOrBlank()) return ""
    return try {
        val past = Instant.parse(isoTimestamp)
        val now = Clock.System.now()
        val seconds = (now - past).inWholeSeconds
        val minutes = seconds / 60
        when {
            minutes < 1 -> "now"
            minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            minutes < 1440 -> "${minutes / 60} hour${if (minutes / 60 > 1) "s" else ""} ago"
            minutes < 43200 -> "${minutes / 1440} day${if (minutes / 1440 > 1) "s" else ""} ago"
            minutes < 525600 -> "${minutes / 43200} month${if (minutes / 43200 > 1) "s" else ""} ago"
            else -> "${minutes / 525600} year${if (minutes / 525600 > 1) "s" else ""} ago"
        }
    } catch (e: Exception) {
        "".also {
            e.printStackTrace()
        }
    }
}
