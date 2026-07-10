package com.mohamed.devz.presentation.util

import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime
import org.junit.Assert.*
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Instant

class TimeFormatterTest {

    @Test
    fun `null timestamp returns empty string`() {
        assertEquals("", formatRelativeTime(null))
    }

    @Test
    fun `blank timestamp returns empty string`() {
        assertEquals("", formatRelativeTime(""))
        assertEquals("", formatRelativeTime("   "))
    }

    @Test
    fun `invalid timestamp returns empty string`() {
        assertEquals("", formatRelativeTime("not-a-timestamp"))
    }

    @Test
    fun `just now returns now`() {
        val now = Clock.System.now().toString()
        val result = formatRelativeTime(now)
        assertEquals("now", result)
    }

    @Test
    fun `1 minute ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("60s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("1 minute ago", result)
    }

    @Test
    fun `5 minutes ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("300s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("5 minutes ago", result)
    }

    @Test
    fun `1 hour ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("3600s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("1 hour ago", result)
    }

    @Test
    fun `3 hours ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("10800s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("3 hours ago", result)
    }

    @Test
    fun `1 day ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("86400s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("1 day ago", result)
    }

    @Test
    fun `2 days ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("172800s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("2 days ago", result)
    }

    @Test
    fun `1 month ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("2592000s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("1 month ago", result)
    }

    @Test
    fun `1 year ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("31536000s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("1 year ago", result)
    }

    @Test
    fun `2 years ago returns correct string`() {
        val past = (Clock.System.now() - kotlin.time.Duration.parse("63072000s")).toString()
        val result = formatRelativeTime(past)
        assertEquals("2 years ago", result)
    }
}
