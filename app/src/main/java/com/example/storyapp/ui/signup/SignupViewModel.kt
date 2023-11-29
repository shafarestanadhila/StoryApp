package com.example.storyapp.ui.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.UserRepository

class SignupViewModel (private val userRepository: UserRepository) : ViewModel() {
    fun signupUser(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}