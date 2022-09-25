package com.deanu.storyapp.common.domain.repository

import com.deanu.storyapp.common.data.api.model.ApiLoginResponse
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.data.api.model.LoginResult
import com.deanu.storyapp.common.domain.model.User
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface StoryAppRepository {
    suspend fun registerUser(user: User): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>
    suspend fun loginUser(user: User): NetworkResponse<ApiLoginResponse, ApiLoginResponse>
    suspend fun setLoginState(loginResult: LoginResult)
    fun getLoginState(): Flow<String>
}