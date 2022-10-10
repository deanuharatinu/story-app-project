package com.deanu.storyapp.common.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.domain.repository.StoryAppPagingRepo
import com.haroldadmin.cnradapter.NetworkResponse
import javax.inject.Inject

class StoryAppPagingRepoImpl @Inject constructor(
    private val api: StoryAppApi,
    private val cache: Cache,
    private val storyAppApi: StoryAppApi,
) : StoryAppPagingRepo {
    override suspend fun getStoryListWithPaging(
        token: String,
        page: Int,
        size: Int,
        includeLocation: Int
    ): NetworkResponse<ApiStoryResponse, ApiStoryResponse> {
        return api.getStoryListWithPaging("Bearer $token", page, size, includeLocation)
    }

    override fun getStoryList(token: String): LiveData<PagingData<CachedStory>> {
        @OptIn(ExperimentalPagingApi::class)
        // TODO: page sizenya masih 5 aja
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(cache, storyAppApi, token),
            pagingSourceFactory = { cache.getAllStory() }
        ).liveData
    }
}