package com.deanu.storyapp.common.data.cache

import androidx.paging.PagingSource
import com.deanu.storyapp.common.data.cache.daos.StoryAppDao
import com.deanu.storyapp.common.data.cache.model.CachedStory
import javax.inject.Inject

class RoomCache @Inject constructor(
    private val storyAppDao: StoryAppDao
) : Cache {
    override fun getAllStory(): PagingSource<Int, CachedStory> {
        return storyAppDao.getAllStory()
    }

    override suspend fun insertStory(storyList: List<CachedStory>) {
        storyAppDao.insertStory(storyList)
    }

    override suspend fun deleteAndInsertStory(storyList: List<CachedStory>) {
        storyAppDao.deleteAndInsertStory(storyList)
    }
}