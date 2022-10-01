package com.deanu.storyapp.common.domain.model

import java.io.Serializable

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) : Serializable

data class UploadMessage(
    val error: Boolean,
    val message: String
)

data class BroadcastWidget(
    val storyList: List<Story>
) : Serializable
