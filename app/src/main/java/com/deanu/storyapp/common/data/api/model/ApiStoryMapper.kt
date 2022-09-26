package com.deanu.storyapp.common.data.api.model

import com.deanu.storyapp.common.domain.model.Story
import javax.inject.Inject

class ApiStoryMapper @Inject constructor() : ApiMapper<ApiStory, Story> {
    override fun mapToDomain(apiEntity: ApiStory): Story {
        return Story(
            id = apiEntity.id.orEmpty(),
            name = apiEntity.name.orEmpty(),
            description = apiEntity.description.orEmpty(),
            photoUrl = apiEntity.photoUrl.orEmpty(),
            createdAt = apiEntity.createdAt.orEmpty(),
            lat = apiEntity.lat ?: 0.0,
            lon = apiEntity.lon ?: 0.0
        )
    }
}