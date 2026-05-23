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
            val inserted = remoteDataSource.notification.insertNotification(notification.toData())
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
            val notifications = remoteDataSource.notification.getAllNotificationsByAccountId(accountId)
            Result.Success(notifications.map { it.toDomain() })
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
