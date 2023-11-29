package com.example.storyapp.data.local.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.storyapp.DataDummy
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.model.AddStoryResponse
import com.example.storyapp.data.remote.model.LoginResponse
import com.example.storyapp.data.remote.model.LoginResult
import com.example.storyapp.data.remote.model.RegisterResponse
import com.example.storyapp.data.remote.model.StoryEntity
import com.example.storyapp.data.remote.model.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private lateinit var apiService: ApiService
    private lateinit var storyDatabase: StoryDatabase

    private val token = "dummyToken"

    @Before
    fun setup() {
        apiService = createFakeApiService()
        storyDatabase = createInMemoryStoryDatabase()
    }

    @After
    fun cleanup() {
        storyDatabase.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val remoteMediator = StoryRemoteMediator(token, apiService, storyDatabase)
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    private fun createFakeApiService(): ApiService {
        return object : ApiService {
            override suspend fun login(email: String, password: String): LoginResponse {
                return LoginResponse(LoginResult("dummyToken"), false, "Success")
            }

            override suspend fun register(name: String, email: String, password: String): RegisterResponse {
                return RegisterResponse(false, "User created")
            }

            override suspend fun getStories(
                token: String,
                page: Int?,
                size: Int?,
                location: Int?
            ): StoryResponse {
                val storyList = DataDummy.generateDummyStoryEntityList()
                return StoryResponse(storyList, false, "Stories fetched successfully")
            }

            override suspend fun getStories(token: String, location: Int): StoryResponse {
                val storyList = DataDummy.generateDummyStoryEntityList()
                return StoryResponse(storyList, false, "Stories fetched successfully")
            }

            override suspend fun addStory(
                token: String,
                file: MultipartBody.Part,
                description: RequestBody,
                lat: RequestBody?,
                lon: RequestBody?
            ): AddStoryResponse {
                return DataDummy.generateDummyAddStoryResponse()
            }
        }
    }

    private fun createInMemoryStoryDatabase(): StoryDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            StoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }
}