package com.deanu.storyapp.common.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.haroldadmin.cnradapter.NetworkResponse

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val cache: Cache,
    private val storyAppApi: StoryAppApi,
    private val token: String
) : RemoteMediator<Int, CachedStory>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CachedStory>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX

        return try {
            val response = storyAppApi.getStoryListWithPaging(
                "Bearer $token", page, state.config.pageSize, StoryAppRepoImpl.EXCLUDE_LOCATION
            )
            processResponse(response, loadType)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun processResponse(
        response: NetworkResponse<ApiStoryResponse, ApiStoryResponse>,
        loadType: LoadType
    ): MediatorResult {
        return when (response) {
            is NetworkResponse.Success -> {
                val storyList = response.body.storyList.orEmpty()
                val cachedStory = storyList.map {
                    CachedStory.fromApi(it)
                }

                if (loadType == LoadType.REFRESH) {
                    cache.deleteAndInsertStory(cachedStory)
                } else {
                    cache.insertStory(cachedStory)
                }

                MediatorResult.Success(endOfPaginationReached = storyList.isEmpty())
            }
            is NetworkResponse.Error -> {
                MediatorResult.Error(Exception("Server error"))
            }
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}