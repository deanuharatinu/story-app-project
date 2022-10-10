package com.deanu.storyapp.common.data.api

import com.deanu.storyapp.common.data.api.model.*
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoryAppApi {
    @POST(ApiConstants.REGISTER)
    suspend fun registerAccount(@Body apiRegisterRequest: ApiRegisterRequest):
            NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>

    @POST(ApiConstants.LOGIN)
    suspend fun loginAccount(@Body apiLoginRequest: ApiLoginRequest):
            NetworkResponse<ApiLoginResponse, ApiLoginResponse>

    @GET(ApiConstants.STORY)
    suspend fun getStoryList(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): NetworkResponse<ApiStoryResponse, ApiStoryResponse>

    @Multipart
    @POST(ApiConstants.STORY)
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Float,
        @Part("lon") longitude: Float
    ): NetworkResponse<ApiAddNewStoryResponse, ApiAddNewStoryResponse>
}