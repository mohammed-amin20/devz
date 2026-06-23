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
import java.security.MessageDigest

class AccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : AccountRepository {

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(toByteArray()).joinToString("") { "%02x".format(it) }
    }

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
        } catch (_: IOException) {
            Result.Error(Error.Network)
        } catch (_: Exception) {
            Result.Error(Error.Unknown("Unknown error"))
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
            val hashedPassword = password.sha256()
            val account = remoteDataSource.account.getAccountByUsernameAndPassword(username, hashedPassword)
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
            val hashedAccount = account.copy(password = account.password.sha256())
            val inserted = remoteDataSource.account.insertAccount(hashedAccount.toData())
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

    override suspend fun addPoints(accountId: Int, delta: Int): Result<Unit, Error> {
        return try {
            when (val accountResult = getById(accountId)) {
                is Result.Success -> {
                    val updated = accountResult.data.copy(points = accountResult.data.points + delta)
                    update(updated)
                }
                is Result.Error -> Result.Error(accountResult.error, null)
            }
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                else -> Result.Error(Error.Unknown("Unknown error. Try again later."))
            }
        } catch (_: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String, Error> {
        return try {
            val url = remoteDataSource.account.uploadImage(imageBytes, fileName)
            Result.Success(url)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound)
                in 401..403 -> Result.Error(Error.Unauthorized)
                413 -> Result.Error(Error.Unknown("Image file too large"))
                else -> Result.Error(Error.Storage)
            }
        } catch (e: IOException) {
            Result.Error(Error.Network)
        } catch (e: Exception) {
            Result.Error(Error.Storage)
        }
    }
}
