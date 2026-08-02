package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.Notification as DataNotification
import com.mohamed.devz.feature.core.domain.model.Notification as DomainNotification
import org.junit.Assert.*
import org.junit.Test

class NotificationMapperTest {

    private val dataNotification = DataNotification(
        id = 1,
        typeId = 2,
        userId = 100,
        actorId = 200,
        questionId = 300,
        answerId = 400,
        type = "like",
        message = "liked your question",
        isRead = false,
        createdAt = "2024-01-15T10:00:00Z",
    )

    private val domainNotification = DomainNotification(
        id = 1,
        typeId = 2,
        userId = 100,
        actorId = 200,
        questionId = 300,
        answerId = 400,
        type = "like",
        message = "liked your question",
        isRead = false,
        createdAt = "2024-01-15T10:00:00Z",
        actorName = null,
    )

    @Test
    fun `data to domain without enrichment`() {
        val domain = dataNotification.toDomain()
        assertEquals(dataNotification.id, domain.id)
        assertEquals(dataNotification.typeId, domain.typeId)
        assertEquals(dataNotification.userId, domain.userId)
        assertEquals(dataNotification.actorId, domain.actorId)
        assertEquals(dataNotification.questionId, domain.questionId)
        assertEquals(dataNotification.answerId, domain.answerId)
        assertEquals(dataNotification.type, domain.type)
        assertEquals(dataNotification.message, domain.message)
        assertEquals(dataNotification.isRead, domain.isRead)
        assertEquals(dataNotification.createdAt, domain.createdAt)
        assertNull(domain.actorName)
    }

    @Test
    fun `data to domain with enrichment`() {
        val domain = dataNotification.toDomain(actorName = "John")
        assertEquals("John", domain.actorName)
    }

    @Test
    fun `domain to data mapping`() {
        val data = domainNotification.toData()
        assertEquals(domainNotification.id, data.id)
        assertEquals(domainNotification.typeId, data.typeId)
        assertEquals(domainNotification.userId, data.userId)
        assertEquals(domainNotification.actorId, data.actorId)
        assertEquals(domainNotification.questionId, data.questionId)
        assertEquals(domainNotification.answerId, data.answerId)
        assertEquals(domainNotification.type, data.type)
        assertEquals(domainNotification.message, data.message)
        assertEquals(domainNotification.isRead, data.isRead)
        assertEquals(domainNotification.createdAt, data.createdAt)
    }

    @Test
    fun `domain to data to domain roundtrip`() {
        val roundtrip = domainNotification.toData().toDomain()
        assertEquals(domainNotification, roundtrip)
    }

    @Test
    fun `data to domain to data roundtrip`() {
        val roundtrip = dataNotification.toDomain().toData()
        assertEquals(dataNotification, roundtrip)
    }

    @Test
    fun `mapping handles null answerId`() {
        val notif = dataNotification.copy(answerId = null)
        val domain = notif.toDomain()
        assertNull(domain.answerId)
        val back = domain.toData()
        assertNull(back.answerId)
    }
}
