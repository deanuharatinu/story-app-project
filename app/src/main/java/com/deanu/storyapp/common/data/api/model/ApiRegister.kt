package com.deanu.storyapp.common.data.api.model

import com.deanu.storyapp.common.domain.model.User
import com.google.gson.annotations.SerializedName

data class ApiRegisterResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
)

data class ApiRegisterRequest(
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("password")
    val password: String? = null
) {
    companion object {
        fun fromDomain(user: User): ApiRegisterRequest {
            return ApiRegisterRequest(
                name = user.username,
                email = user.email,
                password = user.password
            )
        }
    }
}