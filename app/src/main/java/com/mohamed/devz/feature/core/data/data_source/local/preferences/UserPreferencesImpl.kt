package com.mohamed.devz.feature.core.data.data_source.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import jakarta.inject.Inject
import jakarta.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

private val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

@Singleton
class UserPreferencesManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : UserPreferences {

    override fun observeIsFirstTime(): Flow<Boolean> {
        return context.dataStore.data.map { it[IS_FIRST_TIME] ?: true }
    }

    override fun observeIsLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }

    override suspend fun setNotFirstTime() {
        context.dataStore.edit { it[IS_FIRST_TIME] = false }
    }

    override suspend fun setLoggedIn() {
        context.dataStore.edit { it[IS_LOGGED_IN] = true }
    }

    override suspend fun setLoggedOut() {
        context.dataStore.edit { it[IS_LOGGED_IN] = false }
    }
}
