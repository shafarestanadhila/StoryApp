package com.example.storyapp.ui.add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import java.io.File

class AddViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentImageUriLiveData = MutableLiveData<Uri>()
    val currentImageUriLiveData: LiveData<Uri>
        get() = _currentImageUriLiveData

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    fun addStory(
                 token: String,
                 imageFile: File,
                 description: String,
                 lat: Float? = null,
                 lon: Float? = null
    ) =
        storyRepository.addStory(token, imageFile, description, lat, lon)

    fun setCurrentImageUri(uri: Uri) {
        _currentImageUriLiveData.value = uri
    }
}