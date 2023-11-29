package com.example.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.storyapp.data.local.datastore.LoginPreference
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val LOGIN_PREFERENCE = "login"

object Injection {
    fun provideRepository(context: Context): UserRepository {
        return UserRepository.getInstance(
            provideApiService(),
            provideLoginPreference(context)
        )
    }

    private fun providesPreferenceDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(context, LOGIN_PREFERENCE)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(LOGIN_PREFERENCE) })
    }

    private fun provideLoginPreference(context: Context): LoginPreference = LoginPreference.getInstance(
        providesPreferenceDataStore(context)
    )

    fun provideStoryRepository(context: Context): StoryRepository {
        return StoryRepository.getInstance(provideApiService(), provideDatabase(context))
    }

    private fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }

    private fun provideDatabase(context: Context): StoryDatabase {
        return StoryDatabase.getInstance(context)
    }
}