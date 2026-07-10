package com.mohamed.devz.presentation.util

import com.mohamed.devz.feature.core.presentation.util.UiText
import org.junit.Assert.*
import org.junit.Test

class UiTextTest {

    @Test
    fun `DynamicString returns its value`() {
        val uiText = UiText.DynamicString("test message")
        assertEquals("test message", uiText.value)
    }

    @Test
    fun `DynamicString toString contains value`() {
        val uiText = UiText.DynamicString("hello")
        assertTrue(uiText.toString().contains("hello"))
    }

    @Test
    fun `StringResource holds resId and args`() {
        val uiText = UiText.StringResource(123, listOf("arg1", 42))
        assertEquals(123, uiText.resId)
        assertEquals(2, uiText.args.size)
        assertEquals("arg1", uiText.args[0])
        assertEquals(42, uiText.args[1])
    }

    @Test
    fun `DynamicString equality works`() {
        val a = UiText.DynamicString("same")
        val b = UiText.DynamicString("same")
        val c = UiText.DynamicString("different")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `StringResource equality works`() {
        val a = UiText.StringResource(1, listOf("x"))
        val b = UiText.StringResource(1, listOf("x"))
        val c = UiText.StringResource(2, listOf("x"))
        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}
