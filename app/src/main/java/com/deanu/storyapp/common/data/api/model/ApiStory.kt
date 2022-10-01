package com.deanu.storyapp.common.data.api.model

import com.deanu.storyapp.common.domain.model.UploadMessage
import com.google.gson.annotations.SerializedName

data class ApiStoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("listStory")
    val storyList: List<ApiStory>? = null
)

data class ApiStory(
    @field:SerializedName("id")
    val id: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,
    @field:SerializedName("createdAt")
    val createdAt: String? = null,
    @field:SerializedName("lat")
    val lat: Double? = null,
    @field:SerializedName("lon")
    val lon: Double? = null
)

data class ApiAddNewStoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
) {
    fun toDomain(): UploadMessage {
        return UploadMessage(
            error = this.error ?: false,
            message = this.message.orEmpty()
        )
    }
}