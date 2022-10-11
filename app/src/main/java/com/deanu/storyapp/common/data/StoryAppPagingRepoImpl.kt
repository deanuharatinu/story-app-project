package com.deanu.storyapp.common.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.domain.repository.StoryAppPagingRepo
import javax.inject.Inject

class StoryAppPagingRepoImpl @Inject constructor(
    private val api: StoryAppApi,
    private val cache: Cache,
) : StoryAppPagingRepo {
    override fun getStoryList(
        token: String,
        isInitialRefresh: Boolean
    ): LiveData<PagingData<CachedStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(cache, api, token, isInitialRefresh),
            pagingSourceFactory = { cache.getAllStory() }
        ).liveData
    }
}