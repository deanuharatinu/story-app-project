package com.deanu.storyapp.login

import androidx.lifecycle.*
import com.deanu.storyapp.common.data.api.model.ApiLoginResponse
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoginSuccess = MutableLiveData(false)
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _loginResponse = MutableLiveData<ApiLoginResponse?>()
    val loginResponse: LiveData<ApiLoginResponse?> = _loginResponse

    val isAlreadyLogin = repository.getLoginState().asLiveData()

    fun login(user: User) {
        _isLoading.value = true
        viewModelScope.launch(dispatchersProvider.io()) {
            when (val response = repository.loginUser(user)) {
                is NetworkResponse.Success -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        _isLoginSuccess.value = true
                        _loginResponse.value = response.body
                    }
                    response.body.loginResult?.let { repository.setLoginState(it) }
                }
                is NetworkResponse.Error -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        _isLoginSuccess.value = false
                        _loginResponse.value = response.body
                    }
                }
            }
        }
    }
}