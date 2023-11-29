package com.example.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)

    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    fun checkIfFirstTime(): LiveData<Boolean> {
        return userRepository.isFirstTime()
    }
}