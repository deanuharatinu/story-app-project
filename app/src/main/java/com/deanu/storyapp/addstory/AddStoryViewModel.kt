package com.deanu.storyapp.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deanu.storyapp.common.domain.model.UploadMessage
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.deanu.storyapp.common.utils.reduceFileImage
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    private val _isTakenFromCamera = MutableLiveData(false)

    private val _imageFile = MutableLiveData<File?>()

    private val _addNewStoryResponse = MutableLiveData<UploadMessage>()
    val addNewStoryResponse: LiveData<UploadMessage> = _addNewStoryResponse

    fun setImageUri(imageUri: Uri) {
        _imageUri.value = imageUri
    }

    fun isTakenFromCamera(): Boolean {
        return _isTakenFromCamera.value ?: false
    }

    fun setIsTakenFromCamera(isTakenFromCamera: Boolean) {
        _isTakenFromCamera.value = isTakenFromCamera
    }

    fun setImageFile(imageFile: File) {
        _imageFile.value = imageFile
    }

    fun getImageFile(): File? {
        return _imageFile.value
    }

    fun addNewStory(desc: String, imageFile: File) {
        _isLoading.value = true
        val file = reduceFileImage(imageFile)
        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: Part = Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        viewModelScope.launch(dispatchersProvider.io()) {
            repository.getLoginState().collect { token ->
                when (val response = repository.addNewStory(token, imageMultipart, description)) {
                    is NetworkResponse.Success -> {
                        withContext(dispatchersProvider.main()) {
                            _isLoading.value = false
                            response.body.let {
                                _addNewStoryResponse.value = it.toDomain()
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        withContext(dispatchersProvider.main()) {
                            _isLoading.value = false
                            response.body.let {
                                _addNewStoryResponse.value = it?.toDomain()
                            }
                        }
                    }
                }
            }
        }
    }
}