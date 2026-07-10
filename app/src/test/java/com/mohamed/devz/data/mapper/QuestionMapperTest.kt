package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.Question as DataQuestion
import com.mohamed.devz.feature.core.domain.model.Question as DomainQuestion
import org.junit.Assert.*
import org.junit.Test

class QuestionMapperTest {

    private val domainQuestion = DomainQuestion(
        id = 1,
        title = "How to use Compose?",
        description = "I need help with Jetpack Compose",
        code = "fun main() {}",
        likesCount = 5,
        answersCount = 3,
        tags = "kotlin,compose",
        langTypeId = 1,
        accountId = 42,
        createdAt = "2024-01-15T10:00:00Z",
        likedAccountIds = "10,20,30",
    )

    private val dataQuestion = DataQuestion(
        id = 1,
        title = "How to use Compose?",
        description = "I need help with Jetpack Compose",
        code = "fun main() {}",
        likesCount = 5,
        answersCount = 3,
        tags = "kotlin,compose",
        langTypeId = 1,
        accountId = 42,
        createdAt = "2024-01-15T10:00:00Z",
        likedAccountIds = "10,20,30",
    )

    @Test
    fun `domain to data mapping preserves all fields`() {
        val data = domainQuestion.toData()
        assertEquals(domainQuestion.id, data.id)
        assertEquals(domainQuestion.title, data.title)
        assertEquals(domainQuestion.description, data.description)
        assertEquals(domainQuestion.code, data.code)
        assertEquals(domainQuestion.likesCount, data.likesCount)
        assertEquals(domainQuestion.answersCount, data.answersCount)
        assertEquals(domainQuestion.tags, data.tags)
        assertEquals(domainQuestion.langTypeId, data.langTypeId)
        assertEquals(domainQuestion.accountId, data.accountId)
        assertEquals(domainQuestion.createdAt, data.createdAt)
        assertEquals(domainQuestion.likedAccountIds, data.likedAccountIds)
    }

    @Test
    fun `data to domain mapping preserves all fields`() {
        val domain = dataQuestion.toDomain()
        assertEquals(dataQuestion.id, domain.id)
        assertEquals(dataQuestion.title, domain.title)
        assertEquals(dataQuestion.description, domain.description)
        assertEquals(dataQuestion.code, domain.code)
        assertEquals(dataQuestion.likesCount, domain.likesCount)
        assertEquals(dataQuestion.answersCount, domain.answersCount)
        assertEquals(dataQuestion.tags, domain.tags)
        assertEquals(dataQuestion.langTypeId, domain.langTypeId)
        assertEquals(dataQuestion.accountId, domain.accountId)
        assertEquals(dataQuestion.createdAt, domain.createdAt)
        assertEquals(dataQuestion.likedAccountIds, domain.likedAccountIds)
    }

    @Test
    fun `domain to data to domain roundtrip`() {
        val roundtrip = domainQuestion.toData().toDomain()
        assertEquals(domainQuestion, roundtrip)
    }

    @Test
    fun `data to domain to data roundtrip`() {
        val roundtrip = dataQuestion.toDomain().toData()
        assertEquals(dataQuestion, roundtrip)
    }

    @Test
    fun `mapping handles null createdAt`() {
        val q = domainQuestion.copy(createdAt = null)
        val data = q.toData()
        assertNull(data.createdAt)
        val back = data.toDomain()
        assertNull(back.createdAt)
    }

    @Test
    fun `mapping handles empty likedAccountIds`() {
        val q = domainQuestion.copy(likedAccountIds = "")
        val data = q.toData()
        assertEquals("", data.likedAccountIds)
        val back = data.toDomain()
        assertEquals("", back.likedAccountIds)
    }
}
