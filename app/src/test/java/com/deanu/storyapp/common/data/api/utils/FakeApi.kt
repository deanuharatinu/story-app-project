package com.deanu.storyapp.common.data.api.utils

import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.*
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.mockito.Mockito
import retrofit2.Response

class FakeApi : StoryAppApi {

    override suspend fun registerAccount(apiRegisterRequest: ApiRegisterRequest): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse> {
        val response = Mockito.mock(Response::class.java)
        return if (apiRegisterRequest.email.equals("deanu.alt@gmail.com")) {
            val responseSuccess = ApiRegisterResponse(false, "User created")
            NetworkResponse.Success(responseSuccess, response)
        } else {
            val responseError = ApiRegisterResponse(true, "Email is already taken")
            NetworkResponse.ServerError(responseError, response)
        }
    }

    override suspend fun loginAccount(apiLoginRequest: ApiLoginRequest): NetworkResponse<ApiLoginResponse, ApiLoginResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoryList(token: String): NetworkResponse<ApiStoryResponse, ApiStoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): NetworkResponse<ApiAddNewStoryResponse, ApiAddNewStoryResponse> {
        TODO("Not yet implemented")
    }
}