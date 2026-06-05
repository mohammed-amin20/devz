package com.mohamed.devz.feature.core.data.repository

import android.util.Log
import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : AccountRepository {

    override suspend fun getById(id: Int): Result<Account, Error> {
        return try {
            val account = remoteDataSource.account.getAccountById(id)
            Result.Success(account.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                in 401..403 -> Result.Error(Error.Unauthorized)
                else -> Result.Error(Error.Unknown("Unknown error. Try again later."))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getAll(): Result<List<Account>, Error> {
        return try {
            val accounts = remoteDataSource.account.getAllAccounts()
            Result.Success(accounts.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown("Unknown error. Try again later."))
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getByUsernameAndPassword(
        username: String,
        password: String,
    ): Result<Account?, Error> {
        return try {
            val account = remoteDataSource.account.getAccountByUsernameAndPassword(username, password)
            Result.Success(account?.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                in 401..403 -> Result.Error(Error.Unauthorized)
                else -> Result.Error(Error.Unknown("Unknown error. Try again later."))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun insert(account: Account): Result<Account, Error> {
        return try {
            val inserted = remoteDataSource.account.insertAccount(account.toData())
            Result.Success(inserted.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                409 -> Result.Error(Error.Conflict)
                else -> Result.Error(Error.Unknown("Unknown error. Try again later."))
            }
        } catch (_: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun update(account: Account): Result<Unit, Error> {
        return try {
            remoteDataSource.account.updateAccount(account.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                409 -> Result.Error(Error.Conflict)
                else -> Result.Error(Error.Unknown("Unknown error. Try again later."))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String, Error> {
        return try {
            val url = remoteDataSource.account.uploadImage(imageBytes, fileName)
            Result.Success(url)
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Storage)
        }
    }
}
