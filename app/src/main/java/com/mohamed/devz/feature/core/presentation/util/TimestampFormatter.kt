package com.mohamed.devz.feature.core.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimestamp(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = sdf.parse(iso.take(19))
        val out = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US)
        out.format(date!!)
    } catch (_: Exception) {
        iso.take(10)
    }
}
