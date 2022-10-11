package com.deanu.storyapp.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.api.model.ApiStoryMapper
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.data.utils.DataDummy
import com.deanu.storyapp.common.data.utils.StoryPagingSource
import com.deanu.storyapp.common.data.utils.noopListUpdateCallback
import com.deanu.storyapp.common.domain.repository.StoryAppPagingRepo
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.deanu.storyapp.getOrAwaitValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val apiStoryMapper = ApiStoryMapper()
    private val repository: StoryAppRepository = mock(StoryAppRepository::class.java)
    private val pagingRepo: StoryAppPagingRepo = mock(StoryAppPagingRepo::class.java)
    private lateinit var homeViewModel: HomeViewModel
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVp4d0gweFFPZTdLcUdtN1giLCJpYXQiOjE2NjQxMDYzNDl9.I6YRyyLmaB76CQX5sV9_Lesf9qs7r0nDUWDTi8PlHH8"

    @Before
    fun setup() {
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }

        `when`(repository.getLoginState()).thenReturn(flowOf(token))
        homeViewModel = HomeViewModel(
            repository,
            pagingRepo,
            dispatchersProvider,
            apiStoryMapper
        )
    }

    @Test
    fun `when get story list, should not null and has the same size of data`() = runTest {
        // Given
        val dummyData = DataDummy.generateDummy()
        val pagingData: PagingData<CachedStory> = StoryPagingSource.snapshot(dummyData)
        val expectedValue = MutableLiveData(pagingData)

        // When
        `when`(pagingRepo.getStoryList(token, true))
            .thenReturn(expectedValue)
        val actualValue = homeViewModel.storyListWithPaging(token, true).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualValue)

        // Then
        assertNotNull(differ.snapshot())
        assertEquals(dummyData, differ.snapshot())
        assertEquals(dummyData.size, differ.snapshot().size)
        assertEquals(dummyData[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when get story list with no data, should not null and has empty size`() = runTest {
        // Given
        val dummyData = listOf<CachedStory>()
        val pagingData: PagingData<CachedStory> = StoryPagingSource.snapshot(dummyData)
        val expectedValue = MutableLiveData(pagingData)

        // When
        `when`(pagingRepo.getStoryList(token, true))
            .thenReturn(expectedValue)
        val actualValue = homeViewModel.storyListWithPaging(token, true).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualValue)

        // Then
        assertNotNull(differ.snapshot())
        assertEquals(dummyData, differ.snapshot())
        assertEquals(dummyData.size, differ.snapshot().size)
    }
}