package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.LanguageType as DataLanguageType
import com.mohamed.devz.feature.core.domain.model.LanguageType as DomainLanguageType
import org.junit.Assert.*
import org.junit.Test

class LanguageTypeMapperTest {

    @Test
    fun `domain to data mapping`() {
        val domain = DomainLanguageType(1, "Kotlin")
        val data = domain.toData()
        assertEquals(1, data.id)
        assertEquals("Kotlin", data.type)
    }

    @Test
    fun `data to domain mapping`() {
        val data = DataLanguageType(2, "JavaScript")
        val domain = data.toDomain()
        assertEquals(2, domain.id)
        assertEquals("JavaScript", domain.type)
    }

    @Test
    fun `roundtrip domain to data to domain`() {
        val original = DomainLanguageType(3, "Python")
        val roundtrip = original.toData().toDomain()
        assertEquals(original, roundtrip)
    }

    @Test
    fun `roundtrip data to domain to data`() {
        val original = DataLanguageType(4, "Java")
        val roundtrip = original.toDomain().toData()
        assertEquals(original, roundtrip)
    }
}
