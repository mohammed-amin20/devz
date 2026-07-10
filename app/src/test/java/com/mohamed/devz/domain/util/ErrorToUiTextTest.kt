package com.mohamed.devz.domain.util

import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.toUIText
import com.mohamed.devz.feature.core.presentation.util.UiText
import org.junit.Assert.*
import org.junit.Test

class ErrorToUiTextTest {

    @Test
    fun `NotFound maps to Resource not found`() {
        val uiText = Error.NotFound.toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Resource not found", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Conflict maps to Data already exists`() {
        val uiText = Error.Conflict.toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Data already exists", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Unauthorized maps to Please log in again`() {
        val uiText = Error.Unauthorized.toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Please log in again", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Network maps to Check your internet connection`() {
        val uiText = Error.Network.toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Check your internet connection", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Storage maps to Something went wrong saving data`() {
        val uiText = Error.Storage.toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Something went wrong saving data", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Unknown maps to its message`() {
        val uiText = Error.Unknown("Custom error message").toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("Custom error message", (uiText as UiText.DynamicString).value)
    }

    @Test
    fun `Unknown with empty message maps to empty string`() {
        val uiText = Error.Unknown("").toUIText()
        assertTrue(uiText is UiText.DynamicString)
        assertEquals("", (uiText as UiText.DynamicString).value)
    }
}
