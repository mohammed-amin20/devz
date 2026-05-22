package com.mohamed.devz.feature.core.data.data_source.local.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    fun observeIsFirstTime(): Flow<Boolean>
    fun observeIsLoggedIn(): Flow<Boolean>
    suspend fun setNotFirstTime()
    suspend fun setLoggedIn()
    suspend fun setLoggedOut()
}
