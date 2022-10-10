package com.deanu.storyapp.common.data.api.utils

import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.*
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.mockito.Mockito.mock
import retrofit2.Response

class FakeApi : StoryAppApi {

    override suspend fun registerAccount(apiRegisterRequest: ApiRegisterRequest): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse> {
        val response = mock(Response::class.java)
        return if (apiRegisterRequest.email.equals("deanu.alt@gmail.com")) {
            val responseSuccess = ApiRegisterResponse(false, "User created")
            NetworkResponse.Success(responseSuccess, response)
        } else {
            val responseError = ApiRegisterResponse(true, "Email is already taken")
            NetworkResponse.ServerError(responseError, response)
        }
    }

    override suspend fun loginAccount(apiLoginRequest: ApiLoginRequest): NetworkResponse<ApiLoginResponse, ApiLoginResponse> {
        val response = mock(Response::class.java)
        return if (apiLoginRequest.email.equals("deanu.alt@gmail.com")) {
            val responseSuccess = ApiLoginResponse(
                false, "success",
                mock(LoginResult::class.java)
            )
            NetworkResponse.Success(responseSuccess, response)
        } else {
            val responseError = ApiLoginResponse(true, "User not found", null)
            NetworkResponse.ServerError(responseError, response)
        }
    }

    override suspend fun getStoryList(token: String, location: Int): NetworkResponse<ApiStoryResponse, ApiStoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: Float,
        longitude: Float
    ): NetworkResponse<ApiAddNewStoryResponse, ApiAddNewStoryResponse> {
        val response = mock(Response::class.java)
        return if (token == "Bearer 1234567890") {
            val responseSuccess = ApiAddNewStoryResponse(false, "success")
            NetworkResponse.Success(responseSuccess, response)
        } else {
            val responseError = ApiAddNewStoryResponse(true, "failed")
            NetworkResponse.ServerError(responseError, response)
        }
    }
}