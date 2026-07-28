package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.CompanyProfile
import com.mohamed.devz.feature.core.domain.repository.CompanyProfileRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import java.io.IOException
import javax.inject.Inject

class CompanyProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : CompanyProfileRepository {

    override suspend fun getByAccountId(accountId: Int): Result<CompanyProfile?, Error> {
        return try {
            val data = remoteDataSource.companyProfile.getByAccountId(accountId)
            Result.Success(data?.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun insert(profile: CompanyProfile): Result<CompanyProfile, Error> {
        return try {
            val data = remoteDataSource.companyProfile.insert(profile.toData())
            Result.Success(data.toDomain())
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun update(profile: CompanyProfile): Result<Unit, Error> {
        return try {
            remoteDataSource.companyProfile.update(profile.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }

    override suspend fun getAll(): Result<List<CompanyProfile>, Error> {
        return try {
            val data = remoteDataSource.companyProfile.getAll()
            Result.Success(data.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
        }
    }
}
