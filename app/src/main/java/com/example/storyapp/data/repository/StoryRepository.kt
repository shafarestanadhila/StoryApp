package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.ITEMS_PER_PAGE
import com.example.storyapp.data.Result
import com.example.storyapp.data.local.paging.StoryRemoteMediator
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.model.AddStoryResponse
import com.example.storyapp.data.remote.model.StoryEntity
import com.example.storyapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val database: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String):LiveData<PagingData<StoryEntity>> {
        val pagingSourceFactory = { database.storyDao().getStory() }
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE
            ),
            remoteMediator = StoryRemoteMediator(
                token,
                apiService,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStories(token)
                if (response.error) {
                    emit(Result.Error(response.message))
                } else {
                    val stories = response.listStory
                    emit(Result.Success(stories))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun addStory(token: String, imageFile: File, description: String, lat: Float?, lon: Float?):
            LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)
        val latRequestBody = lat.toString().toRequestBody(textPlainMediaType)
        val lonRequestBody = lon.toString().toRequestBody(textPlainMediaType)

        try {
            val response = apiService.addStory(
                token,
                imageMultiPart,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
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


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, database)
            }.also { instance = it }
    }
}