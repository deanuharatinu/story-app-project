package com.deanu.storyapp.common.data.api

import com.deanu.storyapp.common.data.api.model.*
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface StoryAppApi {
    @POST(ApiConstants.REGISTER)
    suspend fun registerAccount(@Body apiRegisterRequest: ApiRegisterRequest):
            NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>

    @POST(ApiConstants.LOGIN)
    suspend fun loginAccount(@Body apiLoginRequest: ApiLoginRequest):
            NetworkResponse<ApiLoginResponse, ApiLoginResponse>

    @GET(ApiConstants.STORY)
    suspend fun getStoryList(@Header("Authorization") token: String):
            NetworkResponse<ApiStoryResponse, ApiStoryResponse>
}