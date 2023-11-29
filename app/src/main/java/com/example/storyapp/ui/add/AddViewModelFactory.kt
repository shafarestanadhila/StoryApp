package com.example.storyapp.ui.add

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.di.Injection

class AddViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(storyRepository,  userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: AddViewModelFactory? = null

        fun getInstance(
            context: Context,
        ): AddViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddViewModelFactory(
                    Injection.provideStoryRepository(context),
                    Injection.provideRepository(context),
                )
            }.also { instance = it }
    }
}