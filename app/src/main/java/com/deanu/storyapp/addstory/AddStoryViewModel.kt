package com.deanu.storyapp.addstory

import android.net.Uri
import androidx.lifecycle.*
import com.deanu.storyapp.common.domain.repository.StoryAppRepository
import com.deanu.storyapp.common.utils.DispatchersProvider
import com.deanu.storyapp.common.utils.reduceFileImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    private val _isTakenFromCamera = MutableLiveData(false)

    private val _imageFile = MutableLiveData<File?>()

    private val token: LiveData<String> = repository.getLoginState().asLiveData()

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
                repository.addNewStory(token, imageMultipart, description)
            }
        }

    }
}