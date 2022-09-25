package com.deanu.storyapp.common.data.api.model

import com.deanu.storyapp.common.domain.model.User
import com.google.gson.annotations.SerializedName

data class ApiLoginResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null
)

data class LoginResult(
    @field:SerializedName("userId")
    val userId: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("token")
    val token: String? = null
)

data class ApiLoginRequest(
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("password")
    val password: String? = null
) {
    companion object {
        fun fromDomain(user: User): ApiLoginRequest {
            return ApiLoginRequest(
                email = user.email,
                password = user.password
            )
        }
    }
}
