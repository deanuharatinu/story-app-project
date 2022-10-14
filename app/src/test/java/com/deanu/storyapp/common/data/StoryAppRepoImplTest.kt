package com.deanu.storyapp.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.*
import com.deanu.storyapp.common.data.api.utils.FakeStoryApi
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
        api = FakeStoryApi()
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
            mock(RequestBody::class.java),
            0.0F,
            0.0F
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
            mock(RequestBody::class.java), 0.0F, 0.0F
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
            mock(RequestBody::class.java),
            0.0F, 0.0F
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
            mock(RequestBody::class.java),
            0.0F,
            0.0F
        ) as NetworkResponse.ServerError<ApiAddNewStoryResponse, ApiAddNewStoryResponse>
        val actualMessage = response.body?.message

        // Then
        assertEquals(expectedMessage, actualMessage)
    }

    /* Get Story List Test */

    @Test
    fun `when success getStoryList with empty list, should return empty story list`() = runTest {
        /* Given */
        val token = "1234567890"
        val expectedValue = emptyList<ApiStory>().size

        // When
        val response =
            repository.getStoryList(
                token,
                StoryAppRepoImpl.INCLUDE_LOCATION
            ) as NetworkResponse.Success<ApiStoryResponse, ApiStoryResponse>
        val actualValue = response.body.storyList?.size

        // Then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `when failed getStoryList, should return NetworkReponseError`() = runTest {
        // Given
        val token = "failed"

        // When
        val response =
            repository.getStoryList(
                token,
                StoryAppRepoImpl.INCLUDE_LOCATION
            )

        // Then
        assertThat(response, instanceOf(NetworkResponse.Error::class.java))
    }

    @Test
    fun `when failed getStoryList, response message is failed`() = runTest {
        // Given
        val token = "failed"
        val expectedValue = "failed"

        // When
        val response =
            repository.getStoryList(
                token,
                StoryAppRepoImpl.INCLUDE_LOCATION
            ) as NetworkResponse.ServerError<ApiStoryResponse, ApiStoryResponse>
        val actualValue = response.body?.message

        // Then
        assertEquals(expectedValue, actualValue)
    }
}