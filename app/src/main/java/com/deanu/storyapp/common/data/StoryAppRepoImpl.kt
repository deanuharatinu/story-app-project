package com.deanu.storyapp.common.data

import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiRegisterRequest
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class StoryAppRepoImpl @Inject constructor(
    private val api: StoryAppApi
) : StoryAppRepository {

    override suspend fun registerUser(user: User): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse> {
        return api.registerAccount(ApiRegisterRequest.fromDomain(user))
    }
}