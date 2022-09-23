package com.deanu.storyapp.common.data.api

import com.deanu.storyapp.common.data.api.model.ApiRegisterRequest
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StoryAppApi {
    @POST(ApiConstants.REGISTER)
    suspend fun registerAccount(@Body apiRegisterRequest: ApiRegisterRequest): Call<ApiRegisterResponse>
}