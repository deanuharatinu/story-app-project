package com.deanu.storyapp.common.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.data.cache.model.RemoteKeys
import com.haroldadmin.cnradapter.NetworkResponse

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val cache: Cache,
    private val storyAppApi: StoryAppApi,
    private val token: String,
    private val isInitialRefresh: Boolean
) : RemoteMediator<Int, CachedStory>() {
    override suspend fun initialize(): InitializeAction {
        return if (isInitialRefresh) InitializeAction.LAUNCH_INITIAL_REFRESH
        else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CachedStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKeys
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return try {
            val response = storyAppApi.getStoryListWithPaging(
                "Bearer $token", page, state.config.pageSize, StoryAppRepoImpl.EXCLUDE_LOCATION
            )
            processResponse(response, loadType, page)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CachedStory>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            cache.getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CachedStory>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            cache.getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CachedStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                cache.getRemoteKeysId(id)
            }
        }
    }

    private suspend fun processResponse(
        response: NetworkResponse<ApiStoryResponse, ApiStoryResponse>,
        loadType: LoadType,
        page: Int
    ): MediatorResult {
        return when (response) {
            is NetworkResponse.Success -> {
                val storyList = response.body.storyList.orEmpty()
                val cachedStory = storyList.map {
                    CachedStory.fromApi(it)
                }
                val endOfPaginationReached = storyList.isEmpty()

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = cachedStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                if (loadType == LoadType.REFRESH) {
                    cache.deleteAndInsertStory(cachedStory)
                    cache.deleteAndInsertRemoteKeys(keys)
                } else {
                    cache.insertStory(cachedStory)
                    cache.insertRemoteKeys(keys)
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
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