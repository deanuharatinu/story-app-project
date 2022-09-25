package com.deanu.storyapp.common.data

import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.*
import com.deanu.storyapp.common.data.preferences.Preferences
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoryAppRepoImpl @Inject constructor(
    private val api: StoryAppApi,
    private val preferences: Preferences
) : StoryAppRepository {

    override suspend fun registerUser(user: User): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse> {
        return api.registerAccount(ApiRegisterRequest.fromDomain(user))
    }

    override suspend fun loginUser(user: User): NetworkResponse<ApiLoginResponse, ApiLoginResponse> {
        return api.loginAccount(ApiLoginRequest.fromDomain(user))
    }

    override suspend fun setLoginState(loginResult: LoginResult) {
        preferences.setLoginState(loginResult)
    }

    override fun getLoginState(): Flow<String> {
        return preferences.getLoginState()
    }
}