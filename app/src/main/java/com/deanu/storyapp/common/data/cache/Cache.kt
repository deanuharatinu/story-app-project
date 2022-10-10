package com.deanu.storyapp.common.data.cache

import androidx.paging.PagingSource
import com.deanu.storyapp.common.data.cache.model.CachedStory

interface Cache {
    fun getAllStory(): PagingSource<Int, CachedStory>
    suspend fun deleteAndInsertStory(storyList: List<CachedStory>)
    suspend fun insertStory(storyList: List<CachedStory>)
}