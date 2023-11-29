package com.example.storyapp

import com.example.storyapp.data.remote.model.LoginResponse
import com.example.storyapp.data.remote.model.LoginResult
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


    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult("12345"),
            false,
            "success"
        )
    }
}