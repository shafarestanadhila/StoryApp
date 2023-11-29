package com.example.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.remote.model.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStoryAsLiveData(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story")
    fun getStory(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("DELETE FROM story")
    suspend fun deleteAllStory()

    @Query("SELECT COUNT(id) FROM story")
    suspend fun getCount(): Int
}
