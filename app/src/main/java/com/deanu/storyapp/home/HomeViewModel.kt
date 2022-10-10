package com.deanu.storyapp.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.deanu.storyapp.common.data.StoryAppRepoImpl.Companion.EXCLUDE_LOCATION
import com.deanu.storyapp.common.data.api.model.ApiStoryMapper
import com.deanu.storyapp.common.data.api.model.ApiStoryResponse
import com.deanu.storyapp.common.data.cache.model.CachedStory
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.common.domain.repository.StoryAppPagingRepo
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val pagingRepo: StoryAppPagingRepo,
    private val dispatchersProvider: DispatchersProvider,
    private val apiStoryMapper: ApiStoryMapper
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList

    private val _backPressCounter = MutableLiveData(0)
    val backPressCounter: LiveData<Int> = _backPressCounter

    private val _responseMessage = MutableLiveData<ApiStoryResponse>()
    val responseMessage: LiveData<ApiStoryResponse> = _responseMessage

    val token: LiveData<String> = repository.getLoginState().asLiveData()

    private val _adapterPosition = MutableLiveData<Int>()

    fun getAdapterPosition(): Int {
        return _adapterPosition.value ?: -1
    }

    fun setAdapterPosition(adapterPosition: Int) {
        _adapterPosition.value = adapterPosition
    }

    fun getStoryList(token: String) {
        _isLoading.value = true
        viewModelScope.launch(dispatchersProvider.io()) {
            when (val response = repository.getStoryList(token, EXCLUDE_LOCATION)) {
                is NetworkResponse.Success -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        _storyList.value = response.body.storyList?.map { apiStory ->
                            apiStoryMapper.mapToDomain(apiStory)
                        }
                    }
                }
                is NetworkResponse.Error -> {
                    withContext(dispatchersProvider.main()) {
                        _isLoading.value = false
                        response.body?.let {
                            _responseMessage.value = it
                        }
                        _storyList.value = emptyList()
                    }
                }
            }
        }
    }

    fun storyListWithPaging(token: String): LiveData<PagingData<CachedStory>> {
        return pagingRepo.getStoryList(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch(dispatchersProvider.io()) {
            repository.deleteLoginState()
        }
    }

    fun incrementLogoutCounter() {
        _backPressCounter.value = _backPressCounter.value?.plus(1)
    }

    fun resetBackPressCounter() {
        _backPressCounter.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        _backPressCounter.value = null
    }
}