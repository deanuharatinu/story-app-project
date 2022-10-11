package com.deanu.storyapp.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.StoryAppRepoImpl
import com.deanu.storyapp.common.data.api.model.ApiStoryMapper
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.utils.DataDummy
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.deanu.storyapp.getOrAwaitValue
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MapsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val repository: StoryAppRepository = mock(StoryAppRepository::class.java)
    private lateinit var mapsViewModel: MapsViewModel
    private val response = mock(Response::class.java)
    private val token = "1234567890"

    private val responseSuccess = ApiStoryResponse(false, "success", DataDummy.generateApiStory())
    private val responseError = ApiStoryResponse(true, "failed", listOf())

    @Before
    fun setup() {
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }

        `when`(repository.getLoginState()).thenReturn(flowOf(token))
        mapsViewModel =
            MapsViewModel(repository, dispatchersProvider, ApiStoryMapper())
    }

    @Test
    fun `when getStoryList success, story list value should the same`() = runTest {
        // Given
        val expectedValue = MutableLiveData(DataDummy.generateApiStory())
        val response = NetworkResponse.Success<ApiStoryResponse, ApiStoryResponse>(
            responseSuccess,
            response
        )

        // When
        `when`(repository.getStoryList(token, StoryAppRepoImpl.INCLUDE_LOCATION))
            .thenReturn(response)
        mapsViewModel.getStoryList(token)
        val actualValue = mapsViewModel.storyList.getOrAwaitValue()

        // Then
        verify(repository).getStoryList(token, StoryAppRepoImpl.INCLUDE_LOCATION)
        assertEquals(expectedValue.value?.size, actualValue.size)
    }

    @Test
    fun `when getStoryList failed, story list value is an empty list`() = runTest {
        // Given
        val expectedValue = MutableLiveData(emptyList<List<Story>>())
        val response = NetworkResponse.ServerError<ApiStoryResponse, ApiStoryResponse>(
            responseError,
            response
        )

        // When
        `when`(repository.getStoryList(token, StoryAppRepoImpl.INCLUDE_LOCATION))
            .thenReturn(response)
        mapsViewModel.getStoryList(token)
        val actualValue = mapsViewModel.storyList.getOrAwaitValue()

        // Then
        verify(repository).getStoryList(token, StoryAppRepoImpl.INCLUDE_LOCATION)
        assertEquals(expectedValue.value?.size, actualValue.size)
    }
}