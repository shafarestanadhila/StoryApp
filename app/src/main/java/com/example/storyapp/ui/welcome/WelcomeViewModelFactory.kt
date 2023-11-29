package com.example.storyapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.datastore.LoginPreference

class WelcomeViewModelFactory private constructor(private val loginPreference: LoginPreference) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(loginPreference) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: WelcomeViewModelFactory? = null

        fun getInstance(loginPreferences: LoginPreference): WelcomeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: WelcomeViewModelFactory(loginPreferences)
            }.also { instance = it }
    }
}