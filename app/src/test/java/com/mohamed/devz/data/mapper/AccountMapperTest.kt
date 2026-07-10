package com.mohamed.devz.data.mapper

import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.data.model.Account as DataAccount
import com.mohamed.devz.feature.core.domain.model.Account as DomainAccount
import org.junit.Assert.*
import org.junit.Test

class AccountMapperTest {

    private val domainAccount = DomainAccount(
        id = 1,
        username = "johndoe",
        fullName = "John Doe",
        email = "john@example.com",
        password = "hashed_pwd",
        imageUrl = "https://example.com/avatar.jpg",
        bio = "Android developer",
        techStack = "Kotlin,Compose",
        githubUrl = "https://github.com/johndoe",
        linkedInUrl = "https://linkedin.com/in/johndoe",
        websiteUrl = "https://johndoe.dev",
        points = 100,
        fcmToken = "fcm_token_123",
        followerIds = "2,3",
        followingIds = "4,5",
    )

    private val dataAccount = DataAccount(
        id = 1,
        username = "johndoe",
        fullName = "John Doe",
        email = "john@example.com",
        password = "hashed_pwd",
        imageUrl = "https://example.com/avatar.jpg",
        bio = "Android developer",
        techStack = "Kotlin,Compose",
        githubUrl = "https://github.com/johndoe",
        linkedInUrl = "https://linkedin.com/in/johndoe",
        websiteUrl = "https://johndoe.dev",
        points = 100,
        fcmToken = "fcm_token_123",
        followerIds = "2,3",
        followingIds = "4,5",
    )

    @Test
    fun `domain to data mapping preserves all fields`() {
        val data = domainAccount.toData()
        assertEquals(domainAccount.id, data.id)
        assertEquals(domainAccount.username, data.username)
        assertEquals(domainAccount.fullName, data.fullName)
        assertEquals(domainAccount.email, data.email)
        assertEquals(domainAccount.password, data.password)
        assertEquals(domainAccount.imageUrl, data.imageUrl)
        assertEquals(domainAccount.bio, data.bio)
        assertEquals(domainAccount.techStack, data.techStack)
        assertEquals(domainAccount.githubUrl, data.githubUrl)
        assertEquals(domainAccount.linkedInUrl, data.linkedInUrl)
        assertEquals(domainAccount.websiteUrl, data.websiteUrl)
        assertEquals(domainAccount.points, data.points)
        assertEquals(domainAccount.fcmToken, data.fcmToken)
        assertEquals(domainAccount.followerIds, data.followerIds)
        assertEquals(domainAccount.followingIds, data.followingIds)
    }

    @Test
    fun `data to domain mapping preserves all fields`() {
        val domain = dataAccount.toDomain()
        assertEquals(dataAccount.id, domain.id)
        assertEquals(dataAccount.username, domain.username)
        assertEquals(dataAccount.fullName, domain.fullName)
        assertEquals(dataAccount.email, domain.email)
        assertEquals(dataAccount.password, domain.password)
        assertEquals(dataAccount.imageUrl, domain.imageUrl)
        assertEquals(dataAccount.bio, domain.bio)
        assertEquals(dataAccount.techStack, domain.techStack)
        assertEquals(dataAccount.githubUrl, domain.githubUrl)
        assertEquals(dataAccount.linkedInUrl, domain.linkedInUrl)
        assertEquals(dataAccount.websiteUrl, domain.websiteUrl)
        assertEquals(dataAccount.points, domain.points)
        assertEquals(dataAccount.fcmToken, domain.fcmToken)
        assertEquals(dataAccount.followerIds, domain.followerIds)
        assertEquals(dataAccount.followingIds, domain.followingIds)
    }

    @Test
    fun `domain to data to domain roundtrip`() {
        val roundtrip = domainAccount.toData().toDomain()
        assertEquals(domainAccount, roundtrip)
    }

    @Test
    fun `data to domain to data roundtrip`() {
        val roundtrip = dataAccount.toDomain().toData()
        assertEquals(dataAccount, roundtrip)
    }
}
