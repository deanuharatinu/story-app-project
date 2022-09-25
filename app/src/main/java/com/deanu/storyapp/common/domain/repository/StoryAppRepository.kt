package com.deanu.storyapp.common.domain.repository

import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.domain.model.User
import com.haroldadmin.cnradapter.NetworkResponse

interface StoryAppRepository {

    suspend fun registerUser(user: User): NetworkResponse<ApiRegisterResponse, ApiRegisterResponse>

}