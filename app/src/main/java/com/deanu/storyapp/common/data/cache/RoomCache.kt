package com.deanu.storyapp.common.data.cache

import androidx.paging.PagingSource
import com.deanu.storyapp.common.data.cache.daos.RemoteKeysDao
import com.deanu.storyapp.common.data.cache.daos.StoryAppDao
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.data.cache.model.RemoteKeys
import javax.inject.Inject

class RoomCache @Inject constructor(
    private val storyAppDao: StoryAppDao,
    private val remoteKeysDao: RemoteKeysDao
) : Cache {
    override fun getAllStory(): PagingSource<Int, CachedStory> {
        return storyAppDao.getAllStory()
    }

    override suspend fun insertStory(storyList: List<CachedStory>) {
        storyAppDao.insertStory(storyList)
    }

    override suspend fun getRemoteKeysId(id: String): RemoteKeys? {
        return remoteKeysDao.getRemoteKeysId(id)
    }

    override suspend fun deleteAndInsertStory(storyList: List<CachedStory>) {
        storyAppDao.deleteAndInsertStory(storyList)
    }

    override suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeys>) {
        remoteKeysDao.insertRemoteKeys(remoteKeys)
    }

    override suspend fun deleteAndInsertRemoteKeys(remoteKeys: List<RemoteKeys>) {
        remoteKeysDao.deleteAndInsertRemoteKeys(remoteKeys)
    }

    override suspend fun getStory(): List<CachedStory> {
        return storyAppDao.getStories()
    }
}