package com.deanu.storyapp.common.data

import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiRegisterRequest
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class StoryAppRepoImpl @Inject constructor(
    private val api: StoryAppApi
) : StoryAppRepository {

    override suspend fun registerUser(user: User): ApiRegisterResponse {
//        api.registerAccount(ApiRegisterRequest.fromDomain(user)).enqueue(object: Callback<ApiRegisterResponse> {
//            override fun onResponse(
//                call: Call<ApiRegisterResponse>,
//                response: Response<ApiRegisterResponse>
//            ) {
////                 api.registerAccount(
////                    ApiRegisterRequest.fromDomain(user)
////                )
//            }
//
//            override fun onFailure(call: Call<ApiRegisterResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })

        return ApiRegisterResponse()

    }
}