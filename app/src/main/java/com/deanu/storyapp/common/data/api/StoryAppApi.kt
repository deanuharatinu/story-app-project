package com.deanu.storyapp.common.data.api

import com.deanu.storyapp.common.data.api.model.ApiLoginRequest
import com.deanu.storyapp.common.data.api.model.ApiLoginResponse
import com.deanu.storyapp.common.data.api.model.ApiRegisterRequest
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface StoryAppApi {
    @POST(ApiConstants.REGISTER)
    suspend fun registerAccount(@Body apiRegisterRequest: ApiRegisterRequest):
            NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>

    @POST(ApiConstants.LOGIN)
    suspend fun loginAccount(@Body apiLoginRequest: ApiLoginRequest):
            NetworkResponse<ApiLoginResponse, ApiLoginResponse>
}