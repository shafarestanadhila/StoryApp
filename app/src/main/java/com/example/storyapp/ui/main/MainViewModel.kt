package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository

class MainViewModel (private val userRepository: UserRepository, private val storyRepository: StoryRepository
) : ViewModel() {

    fun checkIfTokenAvailable(): LiveData<String> {
        return userRepository.getToken()
    }

    fun logout() {
        userRepository.deleteToken()
    }

    fun getStory(token: String) = storyRepository.getStory(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}