package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.NotificationType
import com.mohamed.devz.feature.core.domain.repository.NotificationTypeRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class NotificationTypeRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : NotificationTypeRepository {

    override suspend fun getAll(): Result<List<NotificationType>, Error> {
        return try {
            val types = remoteDataSource.notificationType.getAllNotificationTypes()
            Result.Success(types.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }
}
