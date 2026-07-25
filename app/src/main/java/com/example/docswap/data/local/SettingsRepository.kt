package com.example.docswap.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    private val AUTO_DELETE_ENABLED = booleanPreferencesKey("auto_delete_enabled")
    private val AUTO_DELETE_DAYS = androidx.datastore.preferences.core.intPreferencesKey("auto_delete_days")

    val darkModeFlow: Flow<Boolean?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_DARK_MODE]
        }

    val autoDeleteEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[AUTO_DELETE_ENABLED] ?: false }

    val autoDeleteDaysFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[AUTO_DELETE_DAYS] ?: 7 }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }

    suspend fun saveAutoDeleteEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_DELETE_ENABLED] = enabled
        }
    }

    suspend fun saveAutoDeleteDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_DELETE_DAYS] = days
        }
    }
}
