package com.deanu.storyapp.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.deanu.storyapp.getOrAwaitValue
import com.haroldadmin.cnradapter.NetworkResponse
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val repository: StoryAppRepository = mock(StoryAppRepository::class.java)
    private lateinit var registerViewModel: RegisterViewModel
    private val user = mock(User::class.java)
    private val response = mock(Response::class.java)

    private val responseSuccess = ApiRegisterResponse(false, "User created")
    private val responseError = ApiRegisterResponse(true, "Email is already taken")

    @Before
    fun setup() {
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }

        registerViewModel = RegisterViewModel(repository, dispatchersProvider)
    }

    @Test
    fun `when register is success, isUserCreated should True`() = runTest {
        // Given
        val expectedIsUserCreated = MutableLiveData(true)
        val response = NetworkResponse.Success<ApiRegisterResponse, ApiRegisterResponse>(
            responseSuccess,
            response
        )

        // When
        `when`(repository.registerUser(user)).thenReturn(response)
        registerViewModel.registerAccount(user)
        val actualIsUserCreated = registerViewModel.isUserCreated.getOrAwaitValue()

        // Then
        verify(repository).registerUser(user)
        assertEquals(expectedIsUserCreated.value, actualIsUserCreated)
    }

    @Test
    fun `when register is failed, isUserCreated should False`() = runTest {
        // Given
        val expectedIsUserCreated = MutableLiveData(false)
        val response = NetworkResponse.ServerError<ApiRegisterResponse, ApiRegisterResponse>(
            responseError, response
        )

        // When
        `when`(repository.registerUser(user)).thenReturn(response)
        registerViewModel.registerAccount(user)
        val actualIsUserCreated = registerViewModel.isUserCreated.getOrAwaitValue()

        // Then
        verify(repository).registerUser(user)
        assertEquals(expectedIsUserCreated.value, actualIsUserCreated)
    }

    @Test
    fun `when register is success, registerMessage value should the same from response`() =
        runTest {
            // Given
            val expectedRegisterMessage = MutableLiveData("User created")
            val response = NetworkResponse.Success<ApiRegisterResponse, ApiRegisterResponse>(
                responseSuccess,
                response
            )

            // When
            `when`(repository.registerUser(user)).thenReturn(response)
            registerViewModel.registerAccount(user)
            val actualRegisterMessage = registerViewModel.registerMessage.getOrAwaitValue()

            // Then
            verify(repository).registerUser(user)
            assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
        }

    @Test
    fun `when register is failed, registerMessage value should the same from response`() = runTest {
        // Given
        val expectedRegisterMessage = MutableLiveData("Email is already taken")
        val response = NetworkResponse.ServerError<ApiRegisterResponse, ApiRegisterResponse>(
            responseError, response
        )

        // When
        `when`(repository.registerUser(user)).thenReturn(response)
        registerViewModel.registerAccount(user)
        val actualRegisterMessage = registerViewModel.registerMessage.getOrAwaitValue()

        // Then
        verify(repository).registerUser(user)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }
}