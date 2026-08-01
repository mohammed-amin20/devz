package com.mohamed.devz.feature.core.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatTimestamp(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = sdf.parse(iso.take(19))
        val out = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        out.format(date!!)
    } catch (_: Exception) {
        iso.take(10)
    }
}

fun formatTimestampUtcPlus3(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = sdf.parse(iso.take(19))
        val out = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("GMT+3")
        }
        out.format(date!!)
    } catch (_: Exception) {
        iso.take(10)
    }
}
