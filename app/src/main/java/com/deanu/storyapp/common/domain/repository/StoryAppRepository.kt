package com.deanu.storyapp.common.domain.repository

import com.deanu.storyapp.common.data.api.model.*
import com.deanu.storyapp.common.domain.model.User
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryAppRepository {
    suspend fun registerUser(user: User): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>

    suspend fun loginUser(user: User): NetworkResponse<ApiLoginResponse, ApiLoginResponse>

    suspend fun setLoginState(loginResult: LoginResult)

    suspend fun deleteLoginState()

    fun getLoginState(): Flow<String>

    suspend fun getStoryList(token: String, includeLocation: Int): NetworkResponse<ApiStoryResponse, ApiStoryResponse>

    suspend fun addNewStory(
        token: String,
        imageMultiPart: MultipartBody.Part,
        description: RequestBody,
        lat: Float,
        lon: Float
    ): NetworkResponse<ApiAddNewStoryResponse, ApiAddNewStoryResponse>
}