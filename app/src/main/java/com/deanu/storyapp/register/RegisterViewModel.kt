package com.deanu.storyapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerMessage = MutableLiveData<String>()
    val registerMessage: LiveData<String> = _registerMessage

    private val _isUserCreated = MutableLiveData(false)
    val isUserCreated: LiveData<Boolean> = _isUserCreated

    fun registerAccount(user: User) {
        _isLoading.value = true
        viewModelScope.launch(dispatchersProvider.io()) {
            when (val response = repository.registerUser(user)) {
                is NetworkResponse.Success -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        _isUserCreated.value = response.body.error != true
                        _registerMessage.value = response.body.message.orEmpty()
                    }
                }
                is NetworkResponse.Error -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        _isUserCreated.value = response.body?.error != true
                        _registerMessage.value = response.body?.message.orEmpty()
                    }
                }
            }
        }
    }
}