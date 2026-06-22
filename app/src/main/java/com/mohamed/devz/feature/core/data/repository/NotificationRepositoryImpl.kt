package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
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
            val data = notification.toData()
            val inserted = remoteDataSource.notification.insertNotification(data)
            Result.Success(inserted.toDomain())
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
            val allAccounts = remoteDataSource.account.getAllAccounts()
            val accountMap = allAccounts.associateBy { it.id }

            val domains = dataNotifications.map { notification ->
                val actor = accountMap[notification.actorId]
                notification.toDomain(
                    actorName = actor?.username,
                    actorAvatarUrl = actor?.imageUrl,
                )
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
            remoteDataSource.notification.updateNotification(notification.toData())
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
