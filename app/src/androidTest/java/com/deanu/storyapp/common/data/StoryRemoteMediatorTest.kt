package com.deanu.storyapp.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.api.utils.FakeStoryApi
import com.deanu.storyapp.common.data.cache.Cache
import com.deanu.storyapp.common.data.cache.RoomCache
import com.deanu.storyapp.common.data.cache.StoryAppDatabase
import com.deanu.storyapp.common.data.cache.model.CachedStory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors

@MediumTest
@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
class StoryRemoteMediatorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var cache: Cache
    private val isInitialRefresh = true
    private var api = FakeStoryApi()
    private lateinit var storyRemoteMediator: StoryRemoteMediator
    private var mockDb: StoryAppDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryAppDatabase::class.java,
    ).setTransactionExecutor(Executors.newSingleThreadExecutor()).build()

    @Before
    fun setup() {
        cache = RoomCache(mockDb.storyAppDao(), mockDb.remoteKeysDao())
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        // Given
        storyRemoteMediator = StoryRemoteMediator(cache, api, "more data", isInitialRefresh)
        val pagingState = PagingState<Int, CachedStory>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        // When
        val result = storyRemoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenThereIsNoData() = runTest {
        // Given
        storyRemoteMediator = StoryRemoteMediator(cache, api, "no data", isInitialRefresh)
        val pagingState = PagingState<Int, CachedStory>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        // When
        val result = storyRemoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
    }

    @Test
    fun whenLoadFromMediatorShouldStoreSameDataSizeToDb() = runTest {
        // Given
        storyRemoteMediator = StoryRemoteMediator(cache, api, "more data", isInitialRefresh)
        val pagingState = PagingState<Int, CachedStory>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val expectedSize = 21

        // When
        storyRemoteMediator.load(LoadType.REFRESH, pagingState)
        val actualValue = cache.getStory().size

        // Then
        assertEquals(expectedSize, actualValue)
    }

    @Test
    fun whenNoDataShouldStoreNoDataToDb() = runTest {
        // Given
        storyRemoteMediator = StoryRemoteMediator(cache, api, "no data", isInitialRefresh)
        val pagingState = PagingState<Int, CachedStory>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val expectedSize = 0

        // When
        storyRemoteMediator.load(LoadType.REFRESH, pagingState)
        val actualValue = cache.getStory().size

        // Then
        assertEquals(expectedSize, actualValue)
    }
}