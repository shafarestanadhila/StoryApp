package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.local.datastore.LoginPreference
import com.example.storyapp.data.remote.model.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserRepository private constructor(
    private val apiService: ApiService,
    private val loginPreference: LoginPreference
) : CoroutineScope {

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(
                name,
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getToken(): LiveData<String> = loginPreference.getToken().asLiveData()

    fun deleteToken() {
        launch(Dispatchers.IO) {
            loginPreference.deleteToken()
        }
    }

    fun isFirstTime(): LiveData<Boolean> = loginPreference.isFirstTime().asLiveData()

    fun saveToken(token: String) {
        launch(Dispatchers.IO) {
            loginPreference.saveToken(token)
        }
    }

    fun setFirstTime(firstTime: Boolean) {
        launch(Dispatchers.IO) {
            loginPreference.setFirstTime(firstTime)
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, loginPreferences: LoginPreference) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, loginPreferences)
            }.also { instance = it }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}