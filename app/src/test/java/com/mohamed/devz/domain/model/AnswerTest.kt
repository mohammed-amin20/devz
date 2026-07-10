package com.mohamed.devz.domain.model

import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.model.toggleVote
import org.junit.Assert.*
import org.junit.Test

class AnswerTest {

    private val baseAnswer = Answer(
        id = 1,
        description = "Test answer",
        accepted = false,
        votedIds = "",
        questionId = 1,
        accountId = 1,
        createdAt = null,
    )

    @Test
    fun `toggleVote adds accountId to empty votedIds`() {
        val result = baseAnswer.toggleVote(42)
        assertEquals("42", result.votedIds)
    }

    @Test
    fun `toggleVote adds accountId to existing votedIds`() {
        val answer = baseAnswer.copy(votedIds = "10,20")
        val result = answer.toggleVote(42)
        assertEquals("10,20,42", result.votedIds)
    }

    @Test
    fun `toggleVote removes accountId from votedIds`() {
        val answer = baseAnswer.copy(votedIds = "10,20,42")
        val result = answer.toggleVote(42)
        assertEquals("10,20", result.votedIds)
    }

    @Test
    fun `toggleVote removes only the specified accountId`() {
        val answer = baseAnswer.copy(votedIds = "42,10,42,20")
        val result = answer.toggleVote(42)
        assertEquals("10,20", result.votedIds)
    }

    @Test
    fun `toggleVote preserves other fields`() {
        val result = baseAnswer.toggleVote(42)
        assertEquals(1, result.id)
        assertEquals("Test answer", result.description)
        assertFalse(result.accepted)
        assertEquals(1, result.questionId)
        assertEquals(1, result.accountId)
    }
}
