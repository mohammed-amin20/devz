package com.mohamed.devz.feature.core.data.data_source.local.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    fun observeIsFirstTime(): Flow<Boolean>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeCurrentAccountId(): Flow<Int?>
    fun observeThemeMode(): Flow<String>
    fun observeLanguage(): Flow<String>
    suspend fun setNotFirstTime()
    suspend fun setLoggedIn()
    suspend fun setAccountId(id: Int)
    suspend fun clearAccountId()
    suspend fun setLoggedOut()
    suspend fun setThemeMode(value: String)
    suspend fun setLanguage(value: String)
}
