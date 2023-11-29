package com.example.storyapp

import com.example.storyapp.data.remote.model.AddStoryResponse
import com.example.storyapp.data.remote.model.StoryEntity

object DataDummy {
    fun generateDummyStoryEntityList(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 1..10) {
            val story = StoryEntity(
                "https://github.com/settings/profile",
                "2023-10-31T06:41:06.470Z",
                "User $i",
                "Description of post $i",
                100.0 + i*2,
                "story-$i",
                100.0 + i*2
            )

            storyList.add(story)
        }

        return storyList
    }

    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            error = false,
            message = "Story created successfully"
        )
    }
}