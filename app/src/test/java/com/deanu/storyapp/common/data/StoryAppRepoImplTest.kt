package com.deanu.storyapp.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiAddNewStoryResponse
import com.deanu.storyapp.common.data.api.model.ApiLoginResponse
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.data.api.model.LoginResult
import com.deanu.storyapp.common.data.api.utils.FakeApi
import com.deanu.storyapp.common.data.preferences.utils.FakePreferences
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class StoryAppRepoImplTest {
    private lateinit var api: StoryAppApi
    private lateinit var preferences: FakePreferences
    private lateinit var repository: StoryAppRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        api = FakeApi()
        preferences = FakePreferences()
        repository = StoryAppRepoImpl(api, preferences)
    }

    /* Registration Test */

    @Test
    fun `when registration success, should return NetworkResponseSuccess`() = runTest {
        // Given
        val user = User("deanu", "deanu.alt@gmail.com", "deanudeanu")

        // When
        val response = repository.registerUser(user)

        // Then
        assertThat(response, instanceOf(NetworkResponse.Success::class.java))
    }

    @Test
    fun `when registration success, responseMessage is User created`() = runTest {
        // Given
        val user = User("deanu", "deanu.alt@gmail.com", "deanudeanu")
        val expectedMessage = "User created"

        // When
        val response =
            repository.registerUser(user) as NetworkResponse.Success<ApiRegisterResponse, ApiRegisterResponse>
        val responseMessage = response.body.message

        // Then
        assertEquals(expectedMessage, responseMessage)
    }

    @Test
    fun `when registration failed, should return NetworkResponseError`() = runTest {
        // Given
        val user = User("deanu", "deanu@gmail.com", "deanudeanu")

        // When
        val response = repository.registerUser(user)

        // Then
        assertThat(response, instanceOf(NetworkResponse.Error::class.java))
    }

    @Test
    fun `when registration failed, responseMessage is Email already taken`() = runTest {
        // Given
        val user = User("deanu", "deanu@gmail.com", "deanudeanu")
        val expectedMessage = "Email is already taken"

        // When
        val response =
            repository.registerUser(user) as NetworkResponse.Error<ApiRegisterResponse, ApiRegisterResponse>
        val responseMessage = response.body?.message

        // Then
        assertEquals(expectedMessage, responseMessage)
    }

    /* Login Test */
    @Test
    fun `when login success, should return NetworkResponseSuccess`() = runTest {
        // Given
        val user = User("deanu", "deanu.alt@gmail.com", "deanudeanu")

        // When
        val response = repository.loginUser(user)

        // Then
        assertThat(response, instanceOf(NetworkResponse.Success::class.java))
    }

    @Test
    fun `when login failed, should return NetworkResponseError`() = runTest {
        // Given
        val user = User("deanu", "deanu@gmail.com", "deanudeanu")

        // When
        val response = repository.loginUser(user)

        // Then
        assertThat(response, instanceOf(NetworkResponse.Error::class.java))
    }

    @Test
    fun `when login success, response message is success`() = runTest {
        // Given
        val user = User("deanu", "deanu.alt@gmail.com", "deanudeanu")
        val expectedMessage = "success"

        // When
        val response =
            repository.loginUser(user) as NetworkResponse.Success<ApiLoginResponse, ApiLoginResponse>
        val responseMessage = response.body.message

        // Then
        assertEquals(expectedMessage, responseMessage)
    }

    @Test
    fun `when login failed, response message is User not found`() = runTest {
        // Given
        val user = User("deanu", "deanu@gmail.com", "deanudeanu")
        val expectedMessage = "User not found"

        // When
        val response =
            repository.loginUser(user) as NetworkResponse.ServerError<ApiLoginResponse, ApiLoginResponse>
        val responseMessage = response.body?.message

        // Then
        assertEquals(expectedMessage, responseMessage)
    }

    @Test
    fun `when setLoginState is called, token is set to preferences`() = runTest {
        // Given
        val expectedPrefsValue = "1234567890"
        val loginResult = LoginResult("userId", "name", expectedPrefsValue)

        // When
        repository.setLoginState(loginResult)
        val actualPrefsValue = repository.getLoginState().first()

        // Then
        assertEquals(expectedPrefsValue, actualPrefsValue)
    }

    @Test
    fun `when getLoginState is called before setLoginState, its value should not null`() = runTest {
        // Given
        val expectedPrefsValue = ""

        // When
        val actualPrefsValue = repository.getLoginState().first()

        // Then
        assertNotNull(actualPrefsValue)
        assertEquals(expectedPrefsValue, actualPrefsValue)
    }

    @Test
    fun `when deleteLoginState is called, it should set the preferences to empty string`() =
        runTest {
            // Given
            val expectedPrefsValue = ""

            // When
            repository.deleteLoginState()
            val actualPrefsValue = repository.getLoginState().first()

            // Then
            assertEquals(expectedPrefsValue, actualPrefsValue)
        }

    /* Add Story Test */

    @Test
    fun `when addNewStory is success, should return NetworkResponseSuccess`() = runTest {
        // Given
        val token = "1234567890"

        // When
        val response = repository.addNewStory(
            token,
            mock(MultipartBody.Part::class.java),
            mock(RequestBody::class.java)
        )

        // Then
        assertThat(response, instanceOf(NetworkResponse.Success::class.java))
    }

    @Test
    fun `when addNewStory is success, should return message success`() = runTest {
        // Given
        val token = "1234567890"
        val expectedMessage = "success"

        // When
        val response = repository.addNewStory(
            token,
            mock(MultipartBody.Part::class.java),
            mock(RequestBody::class.java)
        ) as NetworkResponse.Success<ApiAddNewStoryResponse, ApiAddNewStoryResponse>
        val responseMessage = response.body.message

        // Then
        assertEquals(expectedMessage, responseMessage)
    }

    @Test
    fun `when addNewStory is failed, should return NetworkResponseError`() = runTest {
        // Given
        val token = "1234567890failed"

        // When
        val response = repository.addNewStory(
            token,
            mock(MultipartBody.Part::class.java),
            mock(RequestBody::class.java)
        )

        // Then
        assertThat(response, instanceOf(NetworkResponse.Error::class.java))
    }

    @Test
    fun `when addNewStory is failed, should return message failed`() = runTest {
        // Given
        val token = "1234567890failed"
        val expectedMessage = "failed"

        // When
        val response = repository.addNewStory(
            token,
            mock(MultipartBody.Part::class.java),
            mock(RequestBody::class.java)
        ) as NetworkResponse.ServerError<ApiAddNewStoryResponse, ApiAddNewStoryResponse>
        val actualMessage = response.body?.message

        // Then
        assertEquals(expectedMessage, actualMessage)
    }
}