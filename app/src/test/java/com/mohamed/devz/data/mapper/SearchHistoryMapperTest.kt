package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.SearchHistory as DataSearchHistory
import com.mohamed.devz.feature.core.domain.model.SearchHistory as DomainSearchHistory
import org.junit.Assert.*
import org.junit.Test

class SearchHistoryMapperTest {

    @Test
    fun `domain to data mapping preserves fields`() {
        val domain = DomainSearchHistory(id = 1, accountId = 42, query = "compose animation", createdAt = "2024-01-15T10:00:00Z")
        val data = domain.toData()
        assertEquals(1, data.id)
        assertEquals(42, data.accountId)
        assertEquals("compose animation", data.query)
        assertEquals("2024-01-15T10:00:00Z", data.createdAt)
    }

    @Test
    fun `data to domain mapping preserves fields`() {
        val data = DataSearchHistory(id = 2, accountId = 99, query = "navigation", createdAt = "2024-01-16T10:00:00Z")
        val domain = data.toDomain()
        assertEquals(2, domain.id)
        assertEquals(99, domain.accountId)
        assertEquals("navigation", domain.query)
        assertEquals("2024-01-16T10:00:00Z", domain.createdAt)
    }

    @Test
    fun `domain to data converts null createdAt to empty string`() {
        val domain = DomainSearchHistory(id = 3, accountId = 1, query = "test", createdAt = null)
        val data = domain.toData()
        assertEquals("", data.createdAt)
    }

    @Test
    fun `data to domain keeps non-null createdAt`() {
        val data = DataSearchHistory(id = 4, accountId = 1, query = "test", createdAt = "2024-01-17T10:00:00Z")
        val domain = data.toDomain()
        assertEquals("2024-01-17T10:00:00Z", domain.createdAt)
    }

    @Test
    fun `roundtrip domain to data to domain`() {
        val original = DomainSearchHistory(id = 5, accountId = 7, query = "room database", createdAt = "2024-01-18T10:00:00Z")
        val roundtrip = original.toData().toDomain()
        assertEquals(original, roundtrip)
    }

    @Test
    fun `roundtrip data to domain to data`() {
        val original = DataSearchHistory(id = 6, accountId = 3, query = "hilt", createdAt = "2024-01-19T10:00:00Z")
        val roundtrip = original.toDomain().toData()
        assertEquals(original, roundtrip)
    }
}
