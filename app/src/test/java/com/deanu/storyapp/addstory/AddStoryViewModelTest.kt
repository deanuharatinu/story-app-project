package com.deanu.storyapp.addstory

import android.app.Application
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.api.model.ApiAddNewStoryResponse
import com.deanu.storyapp.common.data.utils.MockitoHelper.anyObject
import com.deanu.storyapp.common.data.utils.getImageAsset
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val repository: StoryAppRepository = mock(StoryAppRepository::class.java)
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVp4d0gweFFPZTdLcUdtN1giLCJpYXQiOjE2NjQxMDYzNDl9.I6YRyyLmaB76CQX5sV9_Lesf9qs7r0nDUWDTi8PlHH8"
    private val response = mock(Response::class.java)
    private val responseSuccess = ApiAddNewStoryResponse(false, "success")
    private val responseError = ApiAddNewStoryResponse(true, "failed")

    @Before
    fun setup() {
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }
        `when`(repository.getLoginState()).thenReturn(flowOf(token))
        addStoryViewModel = AddStoryViewModel(repository, dispatchersProvider)
    }

    @Test
    fun `when add new story is success, addNewStoryResponse message is success`() = runTest {
        // Given
        val expectedValue = "success"
        val response = NetworkResponse.Success<ApiAddNewStoryResponse, ApiAddNewStoryResponse>(
            responseSuccess,
            response
        )
        val app = RuntimeEnvironment.getApplication() as Application
        val imageFile = getImageAsset("thunderbird.png", app.applicationContext)
        `when`(
            repository.addNewStory(
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject()
            )
        )
            .thenReturn(response)

        // When
        addStoryViewModel.addNewStory("description text", imageFile, mock(Location::class.java))
        val actualValue = addStoryViewModel.addNewStoryResponse.getOrAwaitValue()

        // Then
        verify(repository).addNewStory(
            anyObject(),
            anyObject(),
            anyObject(),
            anyObject(),
            anyObject()
        )
        assertEquals(expectedValue, actualValue.message)
    }

    @Test
    fun `when add new story is failed, addNewStoryResponse message is failed`() = runTest {
        // Given
        val expectedValue = "failed"
        val response = NetworkResponse.ServerError<ApiAddNewStoryResponse, ApiAddNewStoryResponse>(
            responseError,
            response
        )
        val app = RuntimeEnvironment.getApplication() as Application
        val imageFile = getImageAsset("thunderbird.png", app.applicationContext)
        `when`(
            repository.addNewStory(
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject()
            )
        )
            .thenReturn(response)

        // When
        addStoryViewModel.addNewStory("description text", imageFile, mock(Location::class.java))
        val actualValue = addStoryViewModel.addNewStoryResponse.getOrAwaitValue()

        // Then
        verify(repository).addNewStory(
            anyObject(),
            anyObject(),
            anyObject(),
            anyObject(),
            anyObject()
        )
        assertEquals(expectedValue, actualValue.message)
    }
}