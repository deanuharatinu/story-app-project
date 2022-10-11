package com.deanu.storyapp.common.data.cache

import androidx.paging.PagingSource
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.data.cache.model.RemoteKeys

interface Cache {
    fun getAllStory(): PagingSource<Int, CachedStory>
    suspend fun deleteAndInsertStory(storyList: List<CachedStory>)
    suspend fun insertStory(storyList: List<CachedStory>)
    suspend fun getRemoteKeysId(id: String): RemoteKeys?
    suspend fun deleteAndInsertRemoteKeys(remoteKeys: List<RemoteKeys>)
    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeys>)
}