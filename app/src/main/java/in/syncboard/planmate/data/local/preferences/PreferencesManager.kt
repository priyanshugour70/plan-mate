// Path: app/src/main/java/in/syncboard/planmate/data/local/preferences/PreferencesManager.kt

package `in`.syncboard.planmate.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "planmate_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val CURRENCY_KEY = stringPreferencesKey("currency")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    suspend fun saveLoginState(userId: String, isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun clearLoginState() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(IS_LOGGED_IN_KEY)
        }
    }

    suspend fun getCurrentUserId(): String? {
        return context.dataStore.data.first()[USER_ID_KEY]
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[IS_LOGGED_IN_KEY] ?: false
    }

    fun observeLoginState(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    suspend fun saveThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode
        }
    }

    fun getThemeMode(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY] ?: "SYSTEM"
        }
    }

    suspend fun saveCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }

    fun getCurrency(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[CURRENCY_KEY] ?: "INR"
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    fun getLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }
    }
}