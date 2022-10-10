package com.deanu.storyapp.common.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.haroldadmin.cnradapter.NetworkResponse

interface StoryAppPagingRepo {
    suspend fun getStoryListWithPaging(
        token: String,
        page: Int,
        size: Int,
        includeLocation: Int
    ): NetworkResponse<ApiStoryResponse, ApiStoryResponse>

    fun getStoryList(token: String): LiveData<PagingData<CachedStory>>
}