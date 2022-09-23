package com.deanu.storyapp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
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

    fun registerAccount(user: User) {
        _isLoading.value = true
        viewModelScope.launch(dispatchersProvider.io()) {
            val user = repository.registerUser(user)

            withContext(dispatchersProvider.main()) {

//                if (re)

                _isLoading.value = false
            }
        }
    }
}