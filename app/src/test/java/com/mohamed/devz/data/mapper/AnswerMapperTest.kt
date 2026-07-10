package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.Answer as DataAnswer
import com.mohamed.devz.feature.core.domain.model.Answer as DomainAnswer
import org.junit.Assert.*
import org.junit.Test

class AnswerMapperTest {

    private val domainAnswer = DomainAnswer(
        id = 1,
        description = "Try using remember",
        accepted = true,
        votedIds = "10,20,30",
        questionId = 5,
        accountId = 42,
        createdAt = "2024-01-15T10:30:00Z",
    )

    private val dataAnswer = DataAnswer(
        id = 1,
        description = "Try using remember",
        accepted = true,
        votedIds = "10,20,30",
        questionId = 5,
        accountId = 42,
        createdAt = "2024-01-15T10:30:00Z",
    )

    @Test
    fun `domain to data mapping preserves all fields`() {
        val data = domainAnswer.toData()
        assertEquals(domainAnswer.id, data.id)
        assertEquals(domainAnswer.description, data.description)
        assertEquals(domainAnswer.accepted, data.accepted)
        assertEquals(domainAnswer.votedIds, data.votedIds)
        assertEquals(domainAnswer.questionId, data.questionId)
        assertEquals(domainAnswer.accountId, data.accountId)
        assertEquals(domainAnswer.createdAt, data.createdAt)
    }

    @Test
    fun `data to domain mapping preserves all fields`() {
        val domain = dataAnswer.toDomain()
        assertEquals(dataAnswer.id, domain.id)
        assertEquals(dataAnswer.description, domain.description)
        assertEquals(dataAnswer.accepted, domain.accepted)
        assertEquals(dataAnswer.votedIds, domain.votedIds)
        assertEquals(dataAnswer.questionId, domain.questionId)
        assertEquals(dataAnswer.accountId, domain.accountId)
        assertEquals(dataAnswer.createdAt, domain.createdAt)
    }

    @Test
    fun `domain to data to domain roundtrip`() {
        val roundtrip = domainAnswer.toData().toDomain()
        assertEquals(domainAnswer, roundtrip)
    }

    @Test
    fun `data to domain to data roundtrip`() {
        val roundtrip = dataAnswer.toDomain().toData()
        assertEquals(dataAnswer, roundtrip)
    }

    @Test
    fun `mapping handles null createdAt`() {
        val a = domainAnswer.copy(createdAt = null)
        val data = a.toData()
        assertNull(data.createdAt)
        val back = data.toDomain()
        assertNull(back.createdAt)
    }

    @Test
    fun `mapping handles not accepted answer`() {
        val a = domainAnswer.copy(accepted = false)
        val back = a.toData().toDomain()
        assertFalse(back.accepted)
    }
}
