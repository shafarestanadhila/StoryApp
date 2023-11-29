package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository

class MapsViewModel (
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getStory(token: String) = storyRepository.getStoriesWithLocation(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}