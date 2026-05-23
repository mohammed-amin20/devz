package com.mohamed.devz.feature.core.data.data_source.local.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    fun observeIsFirstTime(): Flow<Boolean>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeCurrentAccountId(): Flow<Int?>
    suspend fun setNotFirstTime()
    suspend fun setLoggedIn()
    suspend fun setAccountId(id: Int)
    suspend fun clearAccountId()
    suspend fun setLoggedOut()
}
