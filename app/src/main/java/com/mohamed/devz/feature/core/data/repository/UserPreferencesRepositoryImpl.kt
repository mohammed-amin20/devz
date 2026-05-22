package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.local.preferences.UserPreferences
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
) : UserPreferencesRepository {

    override fun observeIsFirstTime(): Flow<Boolean> {
        return userPreferences.observeIsFirstTime()
    }

    override fun observeIsLoggedIn(): Flow<Boolean> {
        return userPreferences.observeIsLoggedIn()
    }

    override suspend fun setNotFirstTime(): Result<Unit, Error> {
        return try {
            userPreferences.setNotFirstTime()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Failed to save preference"))
        }
    }

    override suspend fun setLoggedIn(): Result<Unit, Error> {
        return try {
            userPreferences.setLoggedIn()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Failed to save preference"))
        }
    }

    override suspend fun setLoggedOut(): Result<Unit, Error> {
        return try {
            userPreferences.setLoggedOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Failed to save preference"))
        }
    }
}
