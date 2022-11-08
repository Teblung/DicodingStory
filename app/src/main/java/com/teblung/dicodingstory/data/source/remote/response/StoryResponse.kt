package com.teblung.dicodingstory.data.source.remote.response

data class StoryResponse(
    val createdAt: String,
    val description: String,
    val id: String,
    val name: String,
    val photoUrl: String,
    val lat: Double,
    val lon: Double
)

data class StoryListResponse(
    val error: Boolean,
    val listStory: ArrayList<StoryResponse>,
    val message: String
)

data class AddStoryResponse(
    val error: Boolean,
    val message: String
)
