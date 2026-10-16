package com.example.mobilelabs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val darkTheme: Boolean = true,
    val fontScale: Int = 100,
    val language: String = "Русский"
)

object UserSettingsStore {
    private val DARK_THEME = booleanPreferencesKey("dark_theme")
    private val FONT_SCALE = intPreferencesKey("font_scale")
    private val LANGUAGE = stringPreferencesKey("language")

    fun observe(context: Context): Flow<UserSettings> =
        context.settingsDataStore.data.map { prefs ->
            UserSettings(
                darkTheme = prefs[DARK_THEME] ?: true,
                fontScale = prefs[FONT_SCALE] ?: 100,
                language = prefs[LANGUAGE] ?: "Русский"
            )
        }

    suspend fun setDarkTheme(context: Context, value: Boolean) {
        context.settingsDataStore.edit { it[DARK_THEME] = value }
    }

    suspend fun setFontScale(context: Context, value: Int) {
        context.settingsDataStore.edit { it[FONT_SCALE] = value }
    }

    suspend fun setLanguage(context: Context, value: String) {
        context.settingsDataStore.edit { it[LANGUAGE] = value }
    }
}
