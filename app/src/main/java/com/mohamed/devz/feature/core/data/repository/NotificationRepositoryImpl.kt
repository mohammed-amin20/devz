package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : NotificationRepository {

    override suspend fun insert(notification: Notification): Result<Notification, Error> {
        return try {
            val allTypes = remoteDataSource.notificationType.getAllNotificationTypes()
            val typeId = allTypes.find { it.type == notification.type }?.id ?: 1
            val data = com.mohamed.devz.feature.core.data.model.Notification(
                id = notification.id,
                description = notification.description,
                accountId = 0,
                typeId = typeId,
                seen = notification.seen,
                createdAt = notification.createdAt,
            )
            val inserted = remoteDataSource.notification.insertNotification(data)
            val typeString = allTypes.find { it.id == inserted.typeId }?.type ?: "UNKNOWN"
            Result.Success(inserted.toDomain(typeString, null))
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                409 -> Result.Error(Error.Conflict)
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAllByAccountId(accountId: Int): Result<List<Notification>, Error> {
        return try {
            val dataNotifications = remoteDataSource.notification.getAllNotificationsByAccountId(accountId)
            val allTypes = remoteDataSource.notificationType.getAllNotificationTypes()
            val allAccounts = remoteDataSource.account.getAllAccounts()
            val typeMap = allTypes.associateBy { it.id }
            val accountMap = allAccounts.associateBy { it.id }

            val domains = dataNotifications.map { notification ->
                val typeString = typeMap[notification.typeId]?.type ?: "UNKNOWN"
                val actorName = accountMap[notification.accountId]?.let { "${it.username}" }
                notification.toDomain(typeString, actorName)
            }
            Result.Success(domains)
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun update(notification: Notification): Result<Unit, Error> {
        return try {
            val allTypes = remoteDataSource.notificationType.getAllNotificationTypes()
            val typeId = allTypes.find { it.type == notification.type }?.id ?: 1
            val data = com.mohamed.devz.feature.core.data.model.Notification(
                id = notification.id,
                description = notification.description,
                accountId = 0,
                typeId = typeId,
                seen = notification.seen,
                createdAt = notification.createdAt,
            )
            remoteDataSource.notification.updateNotification(data)
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                409 -> Result.Error(Error.Conflict)
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }
}
