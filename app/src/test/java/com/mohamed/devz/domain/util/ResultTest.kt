package com.mohamed.devz.domain.util

import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `Success holds data`() {
        val result: Result.Success<String, Error> = Result.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Error holds error and optional data`() {
        val result: Result.Error<String, Error> = Result.Error(Error.NotFound)
        assertEquals(Error.NotFound, result.error)
        assertNull(result.data)
    }

    @Test
    fun `Error can carry fallback data`() {
        val result: Result.Error<String, Error> = Result.Error(Error.Conflict, "fallback")
        assertEquals(Error.Conflict, result.error)
        assertEquals("fallback", result.data)
    }

    @Test
    fun `Success and Error are different subtypes`() {
        val success: Result<String, Error> = Result.Success("data")
        val failure: Result<String, Error> = Result.Error(Error.Network)
        assertTrue(success is Result.Success)
        assertTrue(failure is Result.Error)
    }
}
