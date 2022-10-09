package com.deanu.storyapp.common.data.preferences.utils

import com.deanu.storyapp.common.data.api.model.LoginResult
import com.deanu.storyapp.common.data.preferences.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreferences : Preferences {
    private var preferences: String? = null

    override suspend fun setLoginState(apiLoginResult: LoginResult) {
        apiLoginResult.token?.let {
            preferences = it
        }
    }

    override fun getLoginState(): Flow<String> {
        return flow {
            if (preferences == null) {
                emit("")
            } else {
                preferences?.let { emit(preferences!!) }
            }
        }
    }

    override suspend fun deleteLoginState() {
        preferences = null
    }
}