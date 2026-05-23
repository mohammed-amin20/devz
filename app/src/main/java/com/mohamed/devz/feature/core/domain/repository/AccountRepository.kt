package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface AccountRepository {
    suspend fun getById(id: Int): Result<Account, Error>
    suspend fun getAll(): Result<List<Account>, Error>
    suspend fun getByUsernameAndPassword(username: String, password: String): Result<Account?, Error>
    suspend fun insert(account: Account): Result<Account, Error>
    suspend fun update(account: Account): Result<Unit, Error>
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String, Error>
}
