package com.deanu.storyapp.common.data.preferences

import com.deanu.storyapp.common.data.api.model.LoginResult
import kotlinx.coroutines.flow.Flow

interface Preferences {
    suspend fun setLoginState(apiLoginResult: LoginResult)
    fun getLoginState(): Flow<String>
}