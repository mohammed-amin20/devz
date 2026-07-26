package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Announcement
import com.mohamed.devz.feature.core.domain.repository.AnnouncementRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import java.io.IOException

class AnnouncementRepositoryImpl(
    private val remoteDataSource: DevZRemoteDataSource,
) : AnnouncementRepository {

    override suspend fun getAll(): Result<List<Announcement>, Error> {
        return try {
            val announcements = remoteDataSource.announcement.getAllAnnouncements()
            Result.Success(announcements.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun insert(announcement: Announcement): Result<Announcement, Error> {
        return try {
            val inserted = remoteDataSource.announcement.insertAnnouncement(announcement.toData())
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

    override suspend fun delete(id: Int): Result<Unit, Error> {
        return try {
            remoteDataSource.announcement.deleteAnnouncement(id)
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }
}
