package com.example.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.remote.model.StoryEntity
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(userRepository, storyRepository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Get Stories Should Not Null`() = mainDispatcherRules.runBlockingTest {
        val dummyToken = DataDummy.generateDummyLoginResponse().loginResult.token
        val dummyStories = DataDummy.generateDummyStoryEntityList()
        val data = PagingData.from(dummyStories)
        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        Mockito.`when`(viewModel.getStory(dummyToken)).thenReturn(stories)
        val actualStories = viewModel.getStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRules.dispatcher,
            workerDispatcher = mainDispatcherRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(storyRepository).getStory(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `When Get Token Should Not Null And Should Return String`() {
        val observer = Observer<String> {}
        try {
            val dummyToken = DataDummy.generateDummyLoginResponse().loginResult.token
            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken
            Mockito.`when`(viewModel.getToken()).thenReturn(expectedToken)

            val actualToken = viewModel.getToken().getOrAwaitValue()
            Mockito.verify(userRepository).getToken()
            Assert.assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            viewModel.getToken().removeObserver(observer)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}

}