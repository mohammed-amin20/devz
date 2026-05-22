package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeIsFirstTime(): Flow<Boolean>
    fun observeIsLoggedIn(): Flow<Boolean>
    suspend fun setNotFirstTime(): Result<Unit, Error>
    suspend fun setLoggedIn(): Result<Unit, Error>
    suspend fun setLoggedOut(): Result<Unit, Error>
}
