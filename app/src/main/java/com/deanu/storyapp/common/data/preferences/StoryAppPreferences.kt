package com.deanu.storyapp.common.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.deanu.storyapp.common.data.api.model.LoginResult
import com.deanu.storyapp.common.data.preferences.StoryAppPreferences.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@Singleton
class StoryAppPreferences @Inject constructor(
    @ApplicationContext context: Context
) : Preferences {
    private val settingDataStore = context.dataStore

    override suspend fun setLoginState(apiLoginResult: LoginResult) {
        settingDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = apiLoginResult.token.orEmpty()
        }
    }

    override fun getLoginState(): Flow<String> {
        return settingDataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    companion object {
        const val PREFERENCES_NAME = "STORY_APP_PREFERENCES"
        private val TOKEN_KEY = stringPreferencesKey("token_key")
    }
}