package com.deanu.storyapp.common.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.deanu.storyapp.common.data.cache.model.CachedStory

interface StoryAppPagingRepo {
    fun getStoryList(token: String, isInitialRefresh: Boolean): LiveData<PagingData<CachedStory>>
}