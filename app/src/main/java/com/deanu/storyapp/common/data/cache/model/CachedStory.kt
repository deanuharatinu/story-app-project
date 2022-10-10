package com.deanu.storyapp.common.data.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deanu.storyapp.common.data.api.model.ApiStory
import com.deanu.storyapp.common.domain.model.Story

@Entity(tableName = "story")
data class CachedStory(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
) {
    fun toDomain(): Story {
        return Story(
            id = this.id,
            name = this.name,
            description = this.description,
            photoUrl = this.photoUrl,
            createdAt = this.createdAt,
            lat = this.lat ?: 0.0,
            lon = this.lon ?: 0.0
        )
    }

    companion object {
        fun fromApi(story: ApiStory): CachedStory {
            return CachedStory(
                id = story.id.orEmpty(),
                name = story.name.orEmpty(),
                description = story.description.orEmpty(),
                photoUrl = story.photoUrl.orEmpty(),
                createdAt = story.createdAt.orEmpty(),
                lat = story.lat,
                lon = story.lon
            )
        }
    }
}