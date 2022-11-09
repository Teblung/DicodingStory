package com.teblung.dicodingstory.utils

import com.teblung.dicodingstory.data.source.remote.response.StoryResponse

object DataDummy {
    fun generateDummyStory(): List<StoryResponse> {
        val newsList: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..8) {
            val news = StoryResponse(
                "2022-11-09T07:00:34.928Z",
                "ini obat",
                "story-$i",
                "z-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1667977234927_WAV3-vAB.jpg",
                0.0,
                0.0
            )
            newsList.add(news)
        }
        return newsList
    }
}