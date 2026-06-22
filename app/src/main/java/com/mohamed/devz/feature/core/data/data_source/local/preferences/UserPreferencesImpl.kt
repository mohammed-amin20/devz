package com.mohamed.devz.feature.core.data.data_source.local.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

private val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
private val CURRENT_ACCOUNT_ID = intPreferencesKey("current_account_id")

class UserPreferencesImpl(
    private val app: Application,
) : UserPreferences {

    override fun observeIsFirstTime(): Flow<Boolean> {
        return app.dataStore.data.map { it[IS_FIRST_TIME] ?: true }
    }

    override fun observeIsLoggedIn(): Flow<Boolean> {
        return app.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    }

    override fun observeCurrentAccountId(): Flow<Int?> {
        return app.dataStore.data.map { it[CURRENT_ACCOUNT_ID] }
    }

    override suspend fun setNotFirstTime() {
        app.dataStore.edit { it[IS_FIRST_TIME] = false }
    }

    override suspend fun setLoggedIn() {
        app.dataStore.edit { it[IS_LOGGED_IN] = true }
    }

    override suspend fun setAccountId(id: Int) {
        app.dataStore.edit { it[CURRENT_ACCOUNT_ID] = id }
    }

    override suspend fun clearAccountId() {
        app.dataStore.edit { it.remove(CURRENT_ACCOUNT_ID) }
    }

    override suspend fun setLoggedOut() {
        app.dataStore.edit { it[IS_LOGGED_IN] = false }
    }
}
