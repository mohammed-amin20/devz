package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeIsFirstTime(): Flow<Boolean>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeCurrentAccountId(): Flow<Int?>
    suspend fun setNotFirstTime(): Result<Unit, Error>
    suspend fun setLoggedIn(): Result<Unit, Error>
    suspend fun setAccountId(id: Int): Result<Unit, Error>
    suspend fun clearAccountId(): Result<Unit, Error>
    suspend fun setLoggedOut(): Result<Unit, Error>
}
