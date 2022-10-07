package com.deanu.storyapp.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deanu.storyapp.common.data.api.StoryAppApi
import com.deanu.storyapp.common.data.api.model.ApiRegisterResponse
import com.deanu.storyapp.common.data.api.utils.FakeApi
import com.deanu.storyapp.common.data.preferences.Preferences
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
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
    private lateinit var repository: StoryAppRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private val preferences: Preferences = mock(Preferences::class.java)

    @Before
    fun setup() {
        api = FakeApi()
        repository = StoryAppRepoImpl(api, preferences)
    }

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
}