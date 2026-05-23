package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class LanguageTypeRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : LanguageTypeRepository {

    override suspend fun getAll(): Result<List<LanguageType>, Error> {
        return try {
            val types = remoteDataSource.languageType.getAllLanguageTypes()
            Result.Success(types.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }
}
