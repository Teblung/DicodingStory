package com.teblung.dicodingstory.data.source.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryResponse(
    val createdAt: String,
    val description: String,
    @PrimaryKey val id: String,
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
