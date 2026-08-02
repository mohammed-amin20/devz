package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Report
import com.mohamed.devz.feature.core.domain.repository.ReportRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import java.io.IOException
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : ReportRepository {

    override suspend fun insert(report: Report): Result<Report, Error> {
        return try {
            val data = remoteDataSource.report.insertReport(report.toData())
            Result.Success(data.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getAll(status: String?): Result<List<Report>, Error> {
        return try {
            val data = remoteDataSource.report.getReportsByStatus(status)
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getByReporterAndTarget(
        reporterId: Int,
        reportedType: String,
        reportedId: Int,
    ): Result<Report?, Error> {
        return try {
            val data = remoteDataSource.report.getReportByReporterAndTarget(reporterId, reportedType, reportedId)
            Result.Success(data?.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun updateStatus(id: Int, status: String): Result<Unit, Error> {
        return try {
            remoteDataSource.report.updateReportStatus(id, status)
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }
}
