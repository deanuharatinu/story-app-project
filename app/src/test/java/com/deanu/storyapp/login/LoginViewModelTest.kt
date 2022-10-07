package com.deanu.storyapp.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.deanu.storyapp.TestCoroutineRule
import com.deanu.storyapp.common.data.api.model.ApiLoginResponse
import com.deanu.storyapp.common.data.api.model.LoginResult
import com.deanu.storyapp.common.domain.model.User
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
class LoginViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val repository: StoryAppRepository = mock(StoryAppRepository::class.java)
    private lateinit var loginViewModel: LoginViewModel
    private val user = mock(User::class.java)
    private val response = mock(Response::class.java)

    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVp4d0gweFFPZTdLcUdtN1giLCJpYXQiOjE2NjQxMDYzNDl9.I6YRyyLmaB76CQX5sV9_Lesf9qs7r0nDUWDTi8PlHH8"
    private val loginResult = LoginResult(
        "user-ZxwH0xQOe7KqGm7X",
        "deanu",
        token
    )
    private val responseSuccess = ApiLoginResponse(false, "success", loginResult)
    private val responseError = ApiLoginResponse(true, "Invalid password")

    @Before
    fun setup() {
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }

        `when`(repository.getLoginState()).thenReturn(flowOf(token))
        loginViewModel = LoginViewModel(repository, dispatchersProvider)
    }

    @Test
    fun `when login success, isLoginSuccess should true`() = runTest {
        // Given
        val expectedIsLoginSuccess = MutableLiveData(true)
        val response = NetworkResponse.Success<ApiLoginResponse, ApiLoginResponse>(
            responseSuccess,
            response
        )

        // When
        `when`(repository.loginUser(user)).thenReturn(response)
        loginViewModel.login(user)
        val actualIsLoginSuccess = loginViewModel.isLoginSuccess.getOrAwaitValue()

        // Then
        verify(repository).loginUser(user)
        assertEquals(expectedIsLoginSuccess.value, actualIsLoginSuccess)
    }

    @Test
    fun `when login success, repository setLogin is called 1 time`() = runTest {
        // Given
        val response = NetworkResponse.Success<ApiLoginResponse, ApiLoginResponse>(
            responseSuccess,
            response
        )
        val loginResult = loginResult

        // When
        `when`(repository.loginUser(user)).thenReturn(response)
        loginViewModel.login(user)

        // Then
        verify(repository).setLoginState(loginResult)
    }

    @Test
    fun `when already login, isAlreadyLogin not empty`() = runTest {
        // Given
        val expectedValue = MutableLiveData(token)

        // When
        val actualValue = loginViewModel.isAlreadyLogin.getOrAwaitValue()

        // Then
        assertEquals(expectedValue.value, actualValue)
    }

    @Test
    fun `when not login, isAlreadyLogin should empty string`() = runTest {
        // Given
        val dispatchersProvider = object : DispatchersProvider {
            override fun io(): CoroutineDispatcher = testCoroutineRule.testDispatcher
        }
        `when`(repository.getLoginState()).thenReturn(flowOf(""))
        loginViewModel = LoginViewModel(repository, dispatchersProvider)
        val expectedValue = MutableLiveData("")

        // When
        val actualValue = loginViewModel.isAlreadyLogin.getOrAwaitValue()

        // Then
        assertEquals(expectedValue.value, actualValue)
    }

    @Test
    fun `when login failed, isLoginSuccess should false`() = runTest {
        // Given
        val expectedIsLoginSuccess = MutableLiveData(false)
        val response = NetworkResponse.ServerError<ApiLoginResponse, ApiLoginResponse>(
            responseError,
            response
        )

        // When
        `when`(repository.loginUser(user)).thenReturn(response)
        loginViewModel.login(user)
        val actualIsLoginSuccess = loginViewModel.isLoginSuccess.getOrAwaitValue()

        // Then
        verify(repository).loginUser(user)
        assertEquals(expectedIsLoginSuccess.value, actualIsLoginSuccess)
    }
}